FROM eclipse-temurin:17-jdk-jammy AS build

WORKDIR /workspace

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

RUN chmod +x mvnw && ./mvnw -B dependency:go-offline

COPY src src

RUN ./mvnw -B clean package -DskipTests

FROM eclipse-temurin:17-jre-jammy AS runtime

RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/* \
    && groupadd --system spring \
    && useradd --system --gid spring spring

WORKDIR /app

COPY --from=build --chown=spring:spring \
    /workspace/target/ecoactions-api-0.0.1-SNAPSHOT.jar \
    app.jar

USER spring:spring

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
    CMD curl --fail --silent --show-error --max-time 4 "http://localhost:${SERVER_PORT:-8080}/api/health" || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
