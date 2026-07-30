FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY target/activity-log-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8107

ENTRYPOINT ["java","-jar","app.jar"]
