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
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
# The JarLauncher class has been relocated to a new package since Spring Boot 3.2.0.
EXPOSE 8083 9093