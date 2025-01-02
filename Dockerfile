FROM azul/zulu-openjdk:21
LABEL authors="sz"

COPY *.jar app.jar

# 创建一个目录来挂载配置文件
RUN mkdir /config

# 设置构建参数
ARG SPRING_PROFILES_ACTIVE

#设置卷挂载点
VOLUME /config

ENTRYPOINT ["java", "-jar", "-Duser.timezone=Asia/Shanghai","-Dfile.encoding=UTF-8", "app.jar", "--spring.profiles.active=${SPRING_PROFILES_ACTIVE}"]