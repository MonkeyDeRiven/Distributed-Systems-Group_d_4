FROM maven:3.8.1-open-11-slim AS build
RUN mkdir -p /app
COPY /app/
WORKDIR /app
RUN mn package


FROM openjdk:11.0-jre-slim
RUN makedir -p /app
COPY -- from=build /app/target/socket-client-1.0.SNAPSHOT.jar /app
CMD ["java", "-jar", "/app/socket-client.iml"]
