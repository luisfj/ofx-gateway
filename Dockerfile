FROM openjdk:21-jdk-slim
EXPOSE 8080
ADD target/ofx-gateway.jar ofx-gateway.jar
ENTRYPOINT [ "java", "-jar", "/ofx-gateway.jar" ]