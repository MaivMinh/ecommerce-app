FROM eclipse-temurin:21 as builder
WORKDIR application
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract
# Sau khi thực thi xong builder, chúng ta có được bên trong folder application là các folder layer của jar.

FROM eclipse-temurin:21
WORKDIR application
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/spring-boot-loader/ ./
COPY --from=builder application/snapshot-dependencies/ ./
COPY --from=builder application/application/ ./

# ==============================
# DOWNLOAD OPENTELEMETRY AGENT
# ==============================
ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar /otel/opentelemetry-javaagent.jar

# ==============================
# OTEL ENV CONFIG
# ==============================
ENV OTEL_SERVICE_NAME=event-service
ENV OTEL_EXPORTER_OTLP_ENDPOINT=http://otel-collector:4317
ENV OTEL_TRACES_EXPORTER=otlp
ENV OTEL_METRICS_EXPORTER=otlp
ENV OTEL_LOGS_EXPORTER=otlp

# ==============================
# JAVA STARTUP
# ==============================
ENTRYPOINT ["java", "-javaagent:/otel/opentelemetry-javaagent.jar", "org.springframework.boot.loader.launch.JarLauncher"]
EXPOSE 8761