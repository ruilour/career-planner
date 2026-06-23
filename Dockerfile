FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY target/career-planner-0.0.1-SNAPSHOT.jar app.jar

# Alpine 设置 UTF-8 和时区
RUN apk add --no-cache tzdata \
    && cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
    && echo "Asia/Shanghai" > /etc/timezone

ENV LANG=C.UTF-8 \
    LC_ALL=C.UTF-8 \
    TZ=Asia/Shanghai

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]