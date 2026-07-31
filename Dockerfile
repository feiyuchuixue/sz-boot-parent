ARG JAVA_VERSION=21
FROM azul/zulu-openjdk:${JAVA_VERSION}-latest
LABEL authors="sz"

COPY *.jar app.jar
# 安装 curl, 用于蓝绿部署的容器健康检查等，并创建配置和日志目录
RUN set -eux; \
    apt-get update && \
    apt-get install -y --no-install-recommends curl && \
    rm -rf /var/lib/apt/lists/* && \
    mkdir -p /config /logs /data

ARG SPRING_PROFILES_ACTIVE=prod
ENV SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE}

# 声明挂载点（配置/日志）
VOLUME ["/config", "/logs", "/data"]

ENTRYPOINT ["sh", "-c", "java -Duser.timezone=Asia/Shanghai -Dfile.encoding=UTF-8 -jar app.jar --spring.profiles.active=${SPRING_PROFILES_ACTIVE}"]
