#!/usr/bin/env bash
set -euo pipefail

: "${APP_NAME:?缺少 APP_NAME}"
: "${MODULE_DIR:?缺少 MODULE_DIR}"
: "${IMAGE_REF:?缺少 IMAGE_REF}"

MAVEN_PROFILE="${MAVEN_PROFILE:-}"
IMAGE_REF_ALIAS="${IMAGE_REF_ALIAS:-}"

log() {
  echo "[ci][build-sz-service-image] $1"
}

log "应用名称: ${APP_NAME}"
log "模块目录: ${MODULE_DIR}"
log "Maven Profile: ${MAVEN_PROFILE:-none}"
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

log "开始构建 Java 25 Docker 镜像: ${IMAGE_REF}"
docker build \
  -f Dockerfile \
  -t "${IMAGE_REF}" \
  "${MODULE_DIR}/target"

version_output=$(docker run --rm --entrypoint java "${IMAGE_REF}" -version 2>&1)
echo "${version_output}"
if ! grep -Eq 'version "25([."-])' <<< "${version_output}"; then
  echo "[ci][build-sz-service-image] 镜像运行时版本不符合预期 Java 25: ${IMAGE_REF}" >&2
  exit 1
fi

docker run --rm --entrypoint sh "${IMAGE_REF}" \
  -c 'test -f /application/application.jar && test -d /application/lib && test ! -f /app.jar'
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
