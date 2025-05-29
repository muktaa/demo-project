FROM maven:3.8.6-openjdk-11 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Download OpenTelemetry agent in build stage (has curl)
RUN curl -L https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar -o opentelemetry-javaagent.jar

FROM openjdk:11-jre-slim
WORKDIR /app

# Copy both JAR file and OpenTelemetry agent from build stage
COPY --from=build /app/target/notification-service-1.0.0.jar app.jar
COPY --from=build /app/opentelemetry-javaagent.jar opentelemetry-javaagent.jar

EXPOSE 8083

ENV JAVA_TOOL_OPTIONS="-javaagent:/app/opentelemetry-javaagent.jar"

ENTRYPOINT ["java", "-jar", "app.jar"] 