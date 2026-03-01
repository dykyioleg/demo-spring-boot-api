FROM openjdk:21
ARG JAR_FILE=target/demo-*.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8080/tcp
ENTRYPOINT ["java","-jar","/app.jar"]
