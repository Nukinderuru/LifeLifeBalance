FROM docker.io/library/gradle:9.3.0-jdk21 AS builder

WORKDIR /app

COPY settings.gradle.kts build.gradle.kts ./
COPY src src

RUN gradle installDist --no-daemon

FROM docker.io/library/eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /app/build/install/inner-council-backend /app

EXPOSE 8080

CMD ["/app/bin/inner-council-backend"]
