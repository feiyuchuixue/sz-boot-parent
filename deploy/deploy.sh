#!/bin/bash
set -e
cd "$(dirname "$0")"

# 加载 .env 文件变量
if [ -f .env ]; then
  # shellcheck disable=SC1091
  source .env
else
  echo "[deploy] 未找到 .env 文件，请先创建并配置相关参数"
  exit 1
fi

log() { echo -e "\033[32m[deploy]\033[0m $1"; }
err() { echo -e "\033[31m[deploy]\033[0m $1"; }

log "拉取最新镜像: $IMAGE_NAME"
docker pull "$IMAGE_NAME"

if ! docker network ls | grep -q "$NETWORK_NAME"; then
  log "docker 网络 $NETWORK_NAME 不存在，自动创建"
  docker network create "$NETWORK_NAME"
fi

if ! docker ps -a --format '{{.Names}}' | grep -q "^${NGINX_CONTAINER}$"; then
  log "nginx 容器不存在，初始化 nginx..."
  docker compose -f "$COMPOSE_FILE" up -d "$NGINX_CONTAINER"
  sleep 2
fi

if [ ! -f "$NGINX_CONF" ]; then
  log "nginx.conf 不存在，用模板生成"
  cp "$NGINX_CONF_TEMPLATE" "$NGINX_CONF"
  docker cp "$NGINX_CONF" "$NGINX_CONTAINER:/etc/nginx/nginx.conf"
  docker exec "$NGINX_CONTAINER" nginx -s reload || true
fi

if ! docker ps -a --format '{{.Names}}' | grep -q "^${GREEN_NAME}$"; then
  log "green 容器不存在，首次部署，直接启动 green"
  docker compose -f "$COMPOSE_FILE" up -d "$GREEN_NAME"
  for i in {1..30}; do
    status=$(docker inspect --format='{{.State.Health.Status}}' "$GREEN_NAME" 2>/dev/null || echo "unknown")
    log "green 健康状态: $status"
    if [ "$status" = "healthy" ]; then
      break
    fi
    sleep 2
  done
  status=$(docker inspect --format='{{.State.Health.Status}}' "$GREEN_NAME")
  if [ "$status" != "healthy" ]; then
    err "❌ green 不健康，首次部署终止"
    exit 1
  fi
  log "✅ 首次部署完成，green healthy"
  exit 0
fi

log "启动 blue 容器（备用）"
docker rm -f "$BLUE_NAME" >/dev/null 2>&1 || true
docker compose -f "$COMPOSE_FILE" up -d "$BLUE_NAME"

log "等待 blue 健康"
for i in {1..30}; do
  status=$(docker inspect --format='{{.State.Health.Status}}' "$BLUE_NAME" 2>/dev/null || echo "unknown")
  log "blue 健康状态: $status"
  if [ "$status" = "healthy" ]; then
    break
  fi
  sleep 2
done

status=$(docker inspect --format='{{.State.Health.Status}}' "$BLUE_NAME")
if [ "$status" != "healthy" ]; then
  err "❌ blue 不健康，保留 green 继续服役，部署终止（已回退）"
  docker rm -f "$BLUE_NAME" >/dev/null 2>&1 || true
  exit 1
fi

log "✅ blue healthy，开始切流量"
cp "$NGINX_CONF_TEMPLATE" "$NGINX_CONF"
sed -i "s@server ${GREEN_NAME}:${HEALTH_PORT} max_fails=3 fail_timeout=5s;@# server ${GREEN_NAME}:${HEALTH_PORT} max_fails=3 fail_timeout=5s;@" "$NGINX_CONF"
sed -i "s@# server ${BLUE_NAME}:${HEALTH_PORT} max_fails=3 fail_timeout=5s;@server ${BLUE_NAME}:${HEALTH_PORT} max_fails=3 fail_timeout=5s;@" "$NGINX_CONF"

docker cp "$NGINX_CONF" "$NGINX_CONTAINER:/etc/nginx/nginx.conf"
docker exec "$NGINX_CONTAINER" nginx -s reload

log "流量已切到 blue"
log "下线旧 green"
docker stop "$GREEN_NAME"
docker rm -f "$GREEN_NAME"
log "将 blue 重命名为 green"
docker rename "$BLUE_NAME" "$GREEN_NAME"
log "切换完成，当前服务运行于容器: $GREEN_NAME"