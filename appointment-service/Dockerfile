FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY target/patient-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8103

ENTRYPOINT ["java","-jar","app.jar"]
