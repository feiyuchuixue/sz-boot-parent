#!/usr/bin/env bash
set -euo pipefail

: "${APP_NAME:?缺少 APP_NAME}"
: "${MODULE_DIR:?缺少 MODULE_DIR}"
: "${IMAGE_REF:?缺少 IMAGE_REF}"

SCRIPT_DIR=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
REPO_ROOT=$(cd -- "${SCRIPT_DIR}/../.." && pwd)
MAVEN_PROFILE="${MAVEN_PROFILE:-}"
IMAGE_REF_ALIAS="${IMAGE_REF_ALIAS:-}"
JAVA_BASE_IMAGE="${JAVA_BASE_IMAGE:-azul-zulu:25-jdk}"
SECURITY_SOURCE_SCRIPT="${SCRIPT_DIR}/harden-java-truststore.sh"
SECURITY_SOURCE_DENYLIST="${REPO_ROOT}/.github/security/java-truststore-denylist.txt"
SECURITY_CONTEXT_DIR="${MODULE_DIR}/target/.java-security"
SECURITY_CONTEXT_SCRIPT="${SECURITY_CONTEXT_DIR}/harden-java-truststore.sh"
SECURITY_CONTEXT_DENYLIST="${SECURITY_CONTEXT_DIR}/java-truststore-denylist.txt"
SECURITY_CONTEXT_OWNED=false

log() {
  echo "[ci][build-sz-service-image] $1"
}

fail() {
  echo "[ci][build-sz-service-image] $1" >&2
  exit 1
}

normalize_repository() {
  local repository=$1
  local first_component

  if [[ "${repository}" != */* ]]; then
    printf 'docker.io/library/%s\n' "${repository}"
    return
  fi

  first_component=${repository%%/*}
  if [[ "${first_component}" == *.* || "${first_component}" == *:* || "${first_component}" == 'localhost' ]]; then
    printf '%s\n' "${repository}"
  else
    printf 'docker.io/%s\n' "${repository}"
  fi
}

repository_from_image_ref() {
  local image_ref=$1
  local repository=${image_ref%@*}
  local last_component=${repository##*/}

  if [[ "${last_component}" == *:* ]]; then
    repository=${repository%:*}
  fi
  printf '%s\n' "${repository}"
}

resolve_java_base_image() {
  local image_ref=$1
  local source_repository normalized_source inspect_output digest_ref digest_repository digest
  local -a matches=()

  source_repository=$(repository_from_image_ref "${image_ref}")
  normalized_source=$(normalize_repository "${source_repository}")

  log "主动拉取发版时最新 Java 基础镜像: ${image_ref}"
  docker pull "${image_ref}"

  inspect_output=$(docker image inspect --format '{{range .RepoDigests}}{{println .}}{{end}}' "${image_ref}")
  while IFS= read -r digest_ref; do
    [ -n "${digest_ref}" ] || continue
    [[ "${digest_ref}" == *@sha256:* ]] || continue
    digest_repository=${digest_ref%@*}
    digest=${digest_ref##*@}
    [ "$(normalize_repository "${digest_repository}")" = "${normalized_source}" ] || continue
    [[ "${digest}" =~ ^sha256:[0-9a-f]{64}$ ]] || continue

    if [[ " ${matches[*]-} " != *" ${digest} "* ]]; then
      matches+=("${digest}")
    fi
  done <<< "${inspect_output}"

  [ "${#matches[@]}" -eq 1 ] ||
    fail "无法为 ${image_ref} 解析唯一可信 digest，匹配数量: ${#matches[@]}"

  JAVA_BASE_DIGEST=${matches[0]}
  RESOLVED_JAVA_BASE_IMAGE="${source_repository}@${JAVA_BASE_DIGEST}"
  log "本次 Java 基础镜像不可变引用: ${RESOLVED_JAVA_BASE_IMAGE}"
}

cleanup_security_context() {
  if [ "${SECURITY_CONTEXT_OWNED}" = true ] && [ -d "${SECURITY_CONTEXT_DIR}" ]; then
    rm -f -- "${SECURITY_CONTEXT_SCRIPT}" "${SECURITY_CONTEXT_DENYLIST}"
    rmdir -- "${SECURITY_CONTEXT_DIR}" 2>/dev/null || true
  fi
}

prepare_security_context() {
  [ -f "${SECURITY_SOURCE_SCRIPT}" ] || fail "缺少 Java truststore 加固脚本: ${SECURITY_SOURCE_SCRIPT}"
  [ -f "${SECURITY_SOURCE_DENYLIST}" ] || fail "缺少 Java truststore 拒绝策略: ${SECURITY_SOURCE_DENYLIST}"
  [ ! -e "${SECURITY_CONTEXT_DIR}" ] || fail "临时安全构建目录已存在，请先检查: ${SECURITY_CONTEXT_DIR}"

  mkdir "${SECURITY_CONTEXT_DIR}"
  SECURITY_CONTEXT_OWNED=true
  cp "${SECURITY_SOURCE_SCRIPT}" "${SECURITY_CONTEXT_SCRIPT}"
  cp "${SECURITY_SOURCE_DENYLIST}" "${SECURITY_CONTEXT_DENYLIST}"
}

trap cleanup_security_context EXIT INT TERM

log "应用名称: ${APP_NAME}"
log "模块目录: ${MODULE_DIR}"
log "Maven Profile: ${MAVEN_PROFILE:-none}"
log "Java 25 浮动基础镜像: ${JAVA_BASE_IMAGE}"
log "Java 25 目标镜像: ${IMAGE_REF}"
if [ -n "${IMAGE_REF_ALIAS}" ]; then
  log "Java 25 镜像别名: ${IMAGE_REF_ALIAS}"
fi

if [ -n "${MAVEN_PROFILE}" ]; then
  log "开始 Maven 打包: mvn -pl ${MODULE_DIR} -am clean package -DskipTests -P${MAVEN_PROFILE}"
  mvn -pl "${MODULE_DIR}" -am clean package -DskipTests -P"${MAVEN_PROFILE}"
else
  log "开始 Maven 打包: mvn -pl ${MODULE_DIR} -am clean package -DskipTests"
  mvn -pl "${MODULE_DIR}" -am clean package -DskipTests
fi

jar_count=$(find "${MODULE_DIR}/target" -maxdepth 1 -type f -name '*.jar' ! -name '*sources.jar' ! -name '*javadoc.jar' | wc -l | tr -d ' ')
if [ "${jar_count}" -ne 1 ]; then
  echo "[ci][build-sz-service-image] 期望 ${MODULE_DIR}/target 下只有一个可运行 jar，实际数量: ${jar_count}" >&2
  find "${MODULE_DIR}/target" -maxdepth 1 -type f -name '*.jar' -print >&2
  exit 1
fi

jar_file=$(find "${MODULE_DIR}/target" -maxdepth 1 -type f \
  -name '*.jar' ! -name '*sources.jar' ! -name '*javadoc.jar' -print -quit)

log "检查 Spring Boot JAR 分层索引: ${jar_file}"
layers_output=$(java -Djarmode=tools -jar "${jar_file}" list-layers)
echo "${layers_output}"

for expected_layer in dependencies spring-boot-loader snapshot-dependencies application; do
  if ! grep -Fxq "${expected_layer}" <<< "${layers_output}"; then
    echo "[ci][build-sz-service-image] JAR 缺少 Spring Boot 分层: ${expected_layer}" >&2
    exit 1
  fi
done

resolve_java_base_image "${JAVA_BASE_IMAGE}"
prepare_security_context

source_revision=$(git -C "${REPO_ROOT}" rev-parse HEAD)
log "开始构建 Java 25 Docker 镜像: ${IMAGE_REF}"
docker build \
  -f Dockerfile \
  --build-arg "JAVA_BASE_IMAGE=${RESOLVED_JAVA_BASE_IMAGE}" \
  --label "org.opencontainers.image.base.name=${JAVA_BASE_IMAGE}" \
  --label "org.opencontainers.image.base.digest=${JAVA_BASE_DIGEST}" \
  --label "org.opencontainers.image.revision=${source_revision}" \
  -t "${IMAGE_REF}" \
  "${MODULE_DIR}/target"

docker run --rm --entrypoint /usr/local/bin/harden-java-truststore "${IMAGE_REF}" --check

version_output=$(docker run --rm --entrypoint java "${IMAGE_REF}" -version 2>&1)
echo "${version_output}"
if ! grep -Eq 'version "25([."-])' <<< "${version_output}"; then
  echo "[ci][build-sz-service-image] 镜像运行时版本不符合预期 Java 25: ${IMAGE_REF}" >&2
  exit 1
fi

docker run --rm --entrypoint sh "${IMAGE_REF}" \
  -c 'test "$(pwd)" = / && test -f /application/application.jar && test -d /application/lib && test ! -f /app.jar'
log "Docker 镜像构建及运行时检查完成: ${IMAGE_REF}"

if [ -n "${IMAGE_REF_ALIAS}" ]; then
  docker tag "${IMAGE_REF}" "${IMAGE_REF_ALIAS}"

  image_id=$(docker image inspect --format '{{.Id}}' "${IMAGE_REF}")
  image_alias_id=$(docker image inspect --format '{{.Id}}' "${IMAGE_REF_ALIAS}")
  if [ "${image_id}" != "${image_alias_id}" ]; then
    echo "[ci][build-sz-service-image] 镜像别名 ID 不一致: ${IMAGE_REF} (${image_id}) != ${IMAGE_REF_ALIAS} (${image_alias_id})" >&2
    exit 1
  fi
  log "同镜像别名成功: ${IMAGE_REF} -> ${IMAGE_REF_ALIAS} (${image_id})"
fi
