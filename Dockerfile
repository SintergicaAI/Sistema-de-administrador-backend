FROM amazoncorretto:17.0.14
WORKDIR /
COPY target/APIV2-0.0.1-SNAPSHOT.jar /app.jar
CMD ["java", "-jar", "app.jar"]