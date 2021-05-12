FROM openjdk:latest

ADD target/docker-spring-app.jar docker-spring-app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "docker-spring-app.jar"]