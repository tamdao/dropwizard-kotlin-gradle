FROM openjdk:23-jdk-slim
COPY config.yml /opt/dropwizard/
COPY build/libs/shadow-1.0-SNAPSHOT-all.jar /opt/dropwizard/
EXPOSE 8080
WORKDIR /opt/dropwizard
CMD ["java", "-jar", "-Done-jar.silent=true", "shadow-1.0-SNAPSHOT-all.jar", "server", "config.yml"]