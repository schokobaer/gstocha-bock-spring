FROM openjdk:8-jdk-alpine
VOLUME /data
COPY target/gstocha-bock-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","./app.jar"]