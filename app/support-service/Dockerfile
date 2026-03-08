FROM eclipse-temurin:21

WORKDIR /application

# nhận jar từ build argument
ARG JAR_FILE=target/*.jar

# copy jar vào container
COPY ${JAR_FILE} application.jar

# ==============================
# DOWNLOAD OPENTELEMETRY AGENT
# ==============================
ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar /otel/opentelemetry-javaagent.jar

# ==============================
# OTEL ENV CONFIG
# ==============================
ENV OTEL_SERVICE_NAME=support-service
ENV OTEL_EXPORTER_OTLP_ENDPOINT=http://otel-collector:4317
ENV OTEL_TRACES_EXPORTER=otlp
ENV OTEL_METRICS_EXPORTER=otlp
ENV OTEL_LOGS_EXPORTER=otlp

# ==============================
# JAVA STARTUP
# ==============================
ENTRYPOINT ["java","-javaagent:/otel/opentelemetry-javaagent.jar","-jar","application.jar"]
EXPOSE 8086 9096