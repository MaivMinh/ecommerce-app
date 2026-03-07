FROM eclipse-temurin:21

WORKDIR application

COPY target/service-discovery-1.0.0.jar application.jar

# ==============================
# DOWNLOAD OPENTELEMETRY AGENT
# ==============================
ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar /otel/opentelemetry-javaagent.jar

# ==============================
# OTEL ENV CONFIG
# ==============================
ENV OTEL_SERVICE_NAME=service-discovery
ENV OTEL_EXPORTER_OTLP_ENDPOINT=http://otel-collector:4317
ENV OTEL_TRACES_EXPORTER=otlp
ENV OTEL_METRICS_EXPORTER=otlp
ENV OTEL_LOGS_EXPORTER=otlp

# ==============================
# JAVA STARTUP
# ==============================
ENTRYPOINT ["java","-javaagent:/otel/opentelemetry-javaagent.jar","-jar","application.jar"]

EXPOSE 8761