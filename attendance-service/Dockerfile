FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY target/attendance-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8106

ENTRYPOINT ["java","-jar","app.jar"]
