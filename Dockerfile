# 使用与运行时一致的 Java 25 镜像提取 Spring Boot 分层内容
FROM azul-zulu:25-jdk AS builder
WORKDIR /builder

COPY *.jar application.jar
RUN java -Djarmode=tools -jar application.jar \
    extract --layers --destination extracted

# 第一阶段保持现有 Java 25 JDK 基础镜像不变，单独验证分层收益
FROM azul-zulu:25-jdk
LABEL authors="sz"

# curl 供蓝绿部署健康检查使用；目录与现有挂载契约保持不变
RUN set -eux; \
    apt-get update && \
    apt-get install -y --no-install-recommends curl && \
    rm -rf /var/lib/apt/lists/* && \
    mkdir -p /config /logs /data

WORKDIR /application

# 从最稳定到最易变化排列，application 必须最后复制
COPY --from=builder /builder/extracted/dependencies/ ./
COPY --from=builder /builder/extracted/spring-boot-loader/ ./
COPY --from=builder /builder/extracted/snapshot-dependencies/ ./
COPY --from=builder /builder/extracted/application/ ./

# 保持原镜像的运行目录契约，使 file:config/... 继续解析到 /config/...
WORKDIR /

ARG SPRING_PROFILES_ACTIVE=prod
ENV SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE}

VOLUME ["/config", "/logs", "/data"]

ENTRYPOINT ["sh", "-c", "exec java -Duser.timezone=Asia/Shanghai -Dfile.encoding=UTF-8 --enable-native-access=ALL-UNNAMED -jar /application/application.jar --spring.profiles.active=${SPRING_PROFILES_ACTIVE}"]
