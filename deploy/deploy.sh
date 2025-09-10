#!/bin/bash
set -euo pipefail
cd "$(dirname "$0")"

log() { echo -e "\033[32m[deploy]\033[0m $1"; }
err() { echo -e "\033[31m[deploy]\033[0m $1"; }

if [ -f .env ]; then
  source .env
else
  err "未找到 .env"
  exit 1
fi

REQUIRED_VARS=(IMAGE_NAME NETWORK_NAME NGINX_CONTAINER COMPOSE_FILE HEALTH_PORT GREEN_NAME BLUE_NAME STATE_FILE UPSTREAM_FILE)
for v in "${REQUIRED_VARS[@]}"; do
  if [ -z "${!v:-}" ]; then
    err "缺少变量: $v"
    exit 1
  fi
done

HEALTH_RETRY="${HEALTH_RETRY:-30}"
HEALTH_INTERVAL="${HEALTH_INTERVAL:-2}"
SPRINGBOOT_LOG_TAIL="${SPRINGBOOT_LOG_TAIL:-100}"
SPRINGBOOT_LOG_TAIL_HEALTH="${SPRINGBOOT_LOG_TAIL_HEALTH:-10}"
PRINT_SPRINGBOOT_LOG="${PRINT_SPRINGBOOT_LOG:-true}"
NGINX_GRACE_PERIOD="${NGINX_GRACE_PERIOD:-5}" # 切换后等待秒数

LOCK_FILE="${LOCK_FILE:-deploy.lock}"
exec 9>"$LOCK_FILE"
if ! flock -n 9; then
  err "已有部署在进行，取消本次部署"
  exit 1
fi

health_check() {
  local cname="$1"
  for ((i=1; i<=HEALTH_RETRY; i++)); do
    local st
    st=$(docker inspect --format='{{.State.Health.Status}}' "$cname" 2>/dev/null || echo "unknown")
    log "$cname 健康状态: $st ($i/$HEALTH_RETRY)"
    if [[ "$st" == "starting" ]] && [[ "$PRINT_SPRINGBOOT_LOG" == "true" ]]; then
      log "SpringBoot 容器 $cname 日志（最近${SPRINGBOOT_LOG_TAIL_HEALTH}行）如下："
      docker logs --tail "$SPRINGBOOT_LOG_TAIL_HEALTH" "$cname"
    fi
    if [ "$st" = "healthy" ]; then
      return 0
    fi
    sleep "$HEALTH_INTERVAL"
  done
  return 1
}

wait_nginx_healthy() {
  local cname="$NGINX_CONTAINER"
  for ((i=1; i<=HEALTH_RETRY; i++)); do
    local state health
    state=$(docker inspect --format='{{.State.Status}}' "$cname" 2>/dev/null || echo "unknown")
    if docker inspect --format='{{json .State.Health}}' "$cname" 2>/dev/null | grep -q "Status"; then
      health=$(docker inspect --format='{{.State.Health.Status}}' "$cname" 2>/dev/null || echo "unknown")
      log "$cname 状态: $state, 健康: $health ($i/$HEALTH_RETRY)"
      if [ "$state" = "running" ] && [ "$health" = "healthy" ]; then
        return 0
      fi
    else
      log "$cname 状态: $state ($i/$HEALTH_RETRY)"
      if [ "$state" = "running" ]; then
        return 0
      fi
    fi
    sleep "$HEALTH_INTERVAL"
  done
  return 1
}

ensure_network() {
  if ! docker network ls | grep -q "$NETWORK_NAME"; then
    log "创建网络: $NETWORK_NAME"
    docker network create "$NETWORK_NAME"
  fi
}

get_active_slot() {
  if [ -f "$STATE_FILE" ]; then
    grep -E '^ACTIVE_SLOT=' "$STATE_FILE" | cut -d= -f2
  else
    echo ""
  fi
}

set_active_slot() {
  echo "ACTIVE_SLOT=$1" > "$STATE_FILE"
}

slot_name_to_container() {
  local slot="$1"
  if [ "$slot" = "green" ]; then
    echo "$GREEN_NAME"
  else
    echo "$BLUE_NAME"
  fi
}

start_slot() {
  local slot="$1"
  local cname
  cname=$(slot_name_to_container "$slot")
  log "准备启动容器: $cname"
  docker rm -f "$cname" >/dev/null 2>&1 || true
  log "执行: docker compose -f $COMPOSE_FILE up -d $cname"
  docker compose -f "$COMPOSE_FILE" up -d "$cname"
  log "容器 $cname 启动命令已执行，等待健康检查..."
  if ! health_check "$cname"; then
    err "❌ $cname 未通过健康检查，移除"
    docker rm -f "$cname" >/dev/null 2>&1 || true
    return 1
  fi
  log "✅ $cname healthy"
  if [[ "$PRINT_SPRINGBOOT_LOG" == "true" ]]; then
    log "SpringBoot 容器 $cname 日志（最近${SPRINGBOOT_LOG_TAIL}行）如下："
    docker logs --tail "$SPRINGBOOT_LOG_TAIL" "$cname"
  fi
  return 0
}

generate_upstream_conf() {
  # upstream 同时包含新旧实例，实现平滑切换
  local slot1="$1"
  local slot2="$2"
  local cname1 cname2
  cname1=$(slot_name_to_container "$slot1")
  cname2=$(slot_name_to_container "$slot2")
  mkdir -p "$(dirname "$UPSTREAM_FILE")"
  local tmp="${UPSTREAM_FILE}.new"
  {
    echo "# 自动生成: $(date '+%F %T')"
    echo "server ${cname1}:${HEALTH_PORT} max_fails=3 fail_timeout=5s;"
    echo "server ${cname2}:${HEALTH_PORT} max_fails=3 fail_timeout=5s;"
  } > "$tmp"
  mv "$tmp" "$UPSTREAM_FILE"
}

generate_upstream_conf_single() {
  # upstream只包含新实例
  local slot="$1"
  local cname
  cname=$(slot_name_to_container "$slot")
  mkdir -p "$(dirname "$UPSTREAM_FILE")"
  local tmp="${UPSTREAM_FILE}.new"
  {
    echo "# 自动生成: $(date '+%F %T')"
    echo "server ${cname}:${HEALTH_PORT} max_fails=3 fail_timeout=5s;"
  } > "$tmp"
  mv "$tmp" "$UPSTREAM_FILE"
}

reload_nginx() {
  log "准备 reload Nginx，等待容器健康..."
  if ! wait_nginx_healthy; then
    err "❌ Nginx 容器未健康，无法 reload"
    return 1
  fi
  log "Nginx 容器健康，执行语法检查..."
  docker exec "$NGINX_CONTAINER" nginx -t || {
    err "❌ Nginx 语法检查失败"
    return 1
  }
  log "Nginx 语法检查通过，执行 reload..."
  docker exec "$NGINX_CONTAINER" nginx -s reload
  log "Nginx reload 完成"
}

remove_slot() {
  local slot="$1"
  local cname
  cname=$(slot_name_to_container "$slot")
  if docker ps -a --format '{{.Names}}' | grep -q "^${cname}$"; then
    log "下线旧容器: $cname"
    docker rm -f "$cname" >/dev/null 2>&1 || true
  fi
}

warmup_slot_optional() {
  local slot="$1"
  local cname
  cname=$(slot_name_to_container "$slot")
  log "预热（可选）: $cname (当前未实现具体逻辑)"
}

wait_any_slot_healthy() {
  local slots=("$@")
  for ((i=1; i<=HEALTH_RETRY; i++)); do
    for slot in "${slots[@]}"; do
      cname=$(slot_name_to_container "$slot")
      st=$(docker inspect --format='{{.State.Health.Status}}' "$cname" 2>/dev/null || echo "unknown")
      log "$cname 健康状态: $st ($i/$HEALTH_RETRY)"
      if [[ "$st" == "starting" ]] && [[ "$PRINT_SPRINGBOOT_LOG" == "true" ]]; then
        log "SpringBoot 容器 $cname 日志（最近${SPRINGBOOT_LOG_TAIL_HEALTH}行）如下："
        docker logs --tail "$SPRINGBOOT_LOG_TAIL_HEALTH" "$cname"
      fi
      if [ "$st" = "healthy" ]; then
        echo "$slot"
        return 0
      fi
    done
    sleep "$HEALTH_INTERVAL"
  done
  echo ""
  return 1
}

log "拉取镜像: $IMAGE_NAME"
docker pull "$IMAGE_NAME"

ensure_network

if ! docker ps -a --format '{{.Names}}' | grep -q "^${NGINX_CONTAINER}$"; then
  log "准备启动 Nginx 服务容器: $NGINX_CONTAINER"
  log "执行: docker compose -f $COMPOSE_FILE up -d $NGINX_CONTAINER"
  docker compose -f "$COMPOSE_FILE" up -d "$NGINX_CONTAINER"
  log "Nginx 容器启动命令已执行，等待健康检查..."
  if ! wait_nginx_healthy; then
    err "Nginx 容器启动未健康，请排查"
    exit 1
  fi
  log "Nginx 容器健康"
fi

ACTIVE_SLOT=$(get_active_slot)
GREEN_EXISTS=false
BLUE_EXISTS=false
docker ps -a --format '{{.Names}}' | grep -q "^${GREEN_NAME}$" && GREEN_EXISTS=true
docker ps -a --format '{{.Names}}' | grep -q "^${BLUE_NAME}$" && BLUE_EXISTS=true

if [ -z "$ACTIVE_SLOT" ]; then
  if ! $GREEN_EXISTS && ! $BLUE_EXISTS; then
    log "首次部署：启动 green"
    if ! start_slot "green"; then
      err "首次部署失败"
      exit 1
    fi
    generate_upstream_conf_single "green"
    if ! reload_nginx; then
      err "Nginx reload 失败（首次），回退"
      exit 1
    fi
    set_active_slot "green"
    log "✅ 首次部署完成，active=green"
    exit 0
  else
    healthy_slot=$(wait_any_slot_healthy "green" "blue")
    if [ -n "$healthy_slot" ]; then
      ACTIVE_SLOT="$healthy_slot"
      set_active_slot "$ACTIVE_SLOT"
    else
      err "无法确定 active 槽位（无健康实例）"
      exit 1
    fi
  fi
fi

log "当前活跃槽位: $ACTIVE_SLOT"

if [ "$ACTIVE_SLOT" = "green" ]; then
  NEW_SLOT="blue"
else
  NEW_SLOT="green"
fi
log "准备部署到非活跃槽位: $NEW_SLOT"

if ! start_slot "$NEW_SLOT"; then
  err "新槽位启动失败，保持现状"
  exit 1
fi

warmup_slot_optional "$NEW_SLOT"

# 1. upstream双实例，reload nginx（流量平滑迁移）
generate_upstream_conf "$NEW_SLOT" "$ACTIVE_SLOT"
if ! reload_nginx; then
  err "切换失败：回退（移除新槽位容器，恢复 upstream 指向旧）"
  remove_slot "$NEW_SLOT"
  generate_upstream_conf_single "$ACTIVE_SLOT"
  if ! reload_nginx; then
    err "回退 reload 也失败，请人工检查"
    exit 1
  fi
  exit 1
fi
log "upstream已包含新旧实例，等待$NGINX_GRACE_PERIOD秒让连接迁移..."
sleep "$NGINX_GRACE_PERIOD"

# 2. upstream只保留新实例，reload nginx
generate_upstream_conf_single "$NEW_SLOT"
if ! reload_nginx; then
  err "最终切换失败，请人工检查"
  exit 1
fi
log "流量已完全切到: $NEW_SLOT"

remove_slot "$ACTIVE_SLOT"
set_active_slot "$NEW_SLOT"
log "✅ 部署完成，新 active: $NEW_SLOT"