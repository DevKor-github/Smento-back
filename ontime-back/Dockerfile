# 빌드 단계: Gradle을 사용하여 JAR 파일을 빌드
FROM gradle:7.4-jdk17 AS build
ENV APP_HOME=/app
WORKDIR $APP_HOME
COPY . ./
RUN ./gradlew build -x test

FROM openjdk:17
WORKDIR /app
COPY --from=build /app/build/libs/ontime-back-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]