FROM maven:3.8.6-openjdk-11 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Download OpenTelemetry agent in build stage
RUN curl -L https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar -o opentelemetry-javaagent.jar

FROM openjdk:11-jre-slim
WORKDIR /app

COPY --from=build /app/target/order-service-1.0.0.jar app.jar
COPY --from=build /app/opentelemetry-javaagent.jar opentelemetry-javaagent.jar

EXPOSE 8082

# Only add the javaagent - no hardcoded config
ENV JAVA_TOOL_OPTIONS="-javaagent:/app/opentelemetry-javaagent.jar"

ENTRYPOINT ["java", "-jar", "app.jar"] 