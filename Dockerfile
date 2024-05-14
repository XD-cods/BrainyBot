FROM amazoncorretto:17-alpine3.12
WORKDIR /bot
COPY target/knowBot-1.0-SNAPSHOT-jar-with-dependencies.jar .
CMD ["java", "-jar", "knowBot-1.0-SNAPSHOT-jar-with-dependencies.jar"]