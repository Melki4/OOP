# Stage 1: build the WAR with Maven
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml ./
COPY src ./src
COPY webapp ./webapp

RUN mvn -B -DskipTests -Pproduction clean package

# Stage 2: run on Tomcat
FROM tomcat:11.0-jdk21-temurin

ENV DB_HOST=host.docker.internal \
    DB_PORT=5432 \
    DB_NAME=postgres \
    DB_USER=postgres \
    DB_PASSWORD=4sfl8gpassword

COPY --from=build /app/target/projectFor3Lab.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080
CMD ["catalina.sh", "run"]