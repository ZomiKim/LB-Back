# 1. 빌드 단계 (Builder stage)
# 489.69 MB
FROM eclipse-temurin:17-jdk AS builder
WORKDIR /app

# Gradle wrapper 및 소스 코드 복사
COPY gradle gradle
COPY gradlew .
COPY gradlew.bat .
COPY build.gradle .
COPY settings.gradle .
COPY src src

# 프로젝트 빌드
RUN ./gradlew bootWar --no-daemon -x test

# 2. 런타임 단계 (Runtime stage)
FROM eclipse-temurin:17-jre
WORKDIR /app

# Builder 단계에서 생성된 JAR 복사
COPY --from=builder /app/build/libs/lastlayersvr-0.0.1-SNAPSHOT.war app.jar

# 3. 타임존 설정 (로그 시간이 한국 시간으로 나오게 함)
ENV TZ=Asia/Seoul

EXPOSE 8080

# 리눅스 환경
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
