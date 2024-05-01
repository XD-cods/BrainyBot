FROM amazoncorretto:17-alpine3.12
WORKDIR /bot
ARG MONGO_URL
ARG DATABASE_NAME
ARG TELEGRAM_USER_TOKEN
ARG TELEGRAM_ADMIN_TOKEN
ENV spring.data.mongodb.uri=${MONGO_URL}
ENV spring.data.mongodb.databaseName=${DATABASE_NAME}
ENV TELEGRAM_USER_TOKEN=${TELEGRAM_USER_TOKEN}
ENV TELEGRAM_ADMIN_TOKEN=${TELEGRAM_ADMIN_TOKEN}
COPY target/knowBot-1.0-SNAPSHOT-jar-with-dependencies.jar .
CMD ["java", "-jar", "knowBot-1.0-SNAPSHOT-jar-with-dependencies.jar"]