#!/usr/bin/env bash
set -euo pipefail

: "${APP_NAME:?缺少 APP_NAME}"
: "${MODULE_DIR:?缺少 MODULE_DIR}"
: "${IMAGE_REF:?缺少 IMAGE_REF}"

MAVEN_PROFILE="${MAVEN_PROFILE:-}"
IMAGE_REF_JDK25="${IMAGE_REF_JDK25:-}"

log() {
  echo "[ci][build-sz-service-image] $1"
}

log "应用名称: ${APP_NAME}"
log "模块目录: ${MODULE_DIR}"
log "Maven Profile: ${MAVEN_PROFILE:-none}"
log "Java 21 目标镜像: ${IMAGE_REF}"
if [ -n "${IMAGE_REF_JDK25}" ]; then
  log "Java 25 目标镜像: ${IMAGE_REF_JDK25}"
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

build_and_verify_image() {
  local java_version="$1"
  local image_ref="$2"
  local version_output

  log "开始构建 Java ${java_version} Docker 镜像: ${image_ref}"
  docker build \
    --build-arg "JAVA_VERSION=${java_version}" \
    -f Dockerfile \
    -t "${image_ref}" \
    "${MODULE_DIR}/target"

  version_output=$(docker run --rm --entrypoint java "${image_ref}" -version 2>&1)
  echo "${version_output}"
  if ! grep -Eq "version \"${java_version}([.\"-])" <<< "${version_output}"; then
    echo "[ci][build-sz-service-image] 镜像运行时版本不符合预期 Java ${java_version}: ${image_ref}" >&2
    exit 1
  fi

  docker run --rm --entrypoint java "${image_ref}" \
    -Djarmode=tools -jar /app.jar list-layers
  log "Docker 镜像构建及运行时检查完成: ${image_ref}"
}

image_jar_hash() {
  docker run --rm --entrypoint sha256sum "$1" /app.jar | awk '{print $1}'
}

build_and_verify_image 21 "${IMAGE_REF}"

if [ -n "${IMAGE_REF_JDK25}" ]; then
  build_and_verify_image 25 "${IMAGE_REF_JDK25}"

  jdk21_jar_hash=$(image_jar_hash "${IMAGE_REF}")
  jdk25_jar_hash=$(image_jar_hash "${IMAGE_REF_JDK25}")
  if [ "${jdk21_jar_hash}" != "${jdk25_jar_hash}" ]; then
    echo "[ci][build-sz-service-image] Java 21/25 镜像内 JAR 哈希不一致" >&2
    echo "Java 21: ${jdk21_jar_hash}" >&2
    echo "Java 25: ${jdk25_jar_hash}" >&2
    exit 1
  fi
  log "Java 21/25 镜像复用同一 JAR: sha256=${jdk21_jar_hash}"
fi
