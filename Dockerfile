FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY target/resume-AI-0.0.1-SNAPSHOT.jar resume-ai.jar

EXPOSE 8080

ENTRYPOINT [ "java","-jar","resume-ai.jar" ]