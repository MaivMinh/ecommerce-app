FROM eclipse-temurin:21

WORKDIR /application

# nhận jar từ build argument
ARG JAR_FILE=target/*.jar

# copy jar vào container
COPY ${JAR_FILE} application.jar

# ==============================
# JAVA STARTUP
# ==============================
ENTRYPOINT ["java","-jar","application.jar"]
EXPOSE 8080