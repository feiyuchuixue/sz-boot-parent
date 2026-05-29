#!/usr/bin/env bash
set -euo pipefail

: "${APP_NAME:?缺少 APP_NAME}"
: "${MODULE_DIR:?缺少 MODULE_DIR}"
: "${IMAGE_REF:?缺少 IMAGE_REF}"

MAVEN_PROFILE="${MAVEN_PROFILE:-}"

log() {
  echo "[ci][build-sz-service-image] $1"
}

log "应用名称: ${APP_NAME}"
log "模块目录: ${MODULE_DIR}"
log "Maven Profile: ${MAVEN_PROFILE:-none}"
log "目标镜像: ${IMAGE_REF}"

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

log "开始 Docker 构建"
docker build -f Dockerfile -t "${IMAGE_REF}" "${MODULE_DIR}/target"
log "Docker 镜像构建完成: ${IMAGE_REF}"