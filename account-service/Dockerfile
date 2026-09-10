FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine

ARG GID=1011
ARG UID=1011

RUN addgroup -g ${GID} -S devops \
&& adduser -u ${UID} -S -D -H -s /sbin/nologin -G devops admin

WORKDIR /app

RUN mkdir -p /app/tmp /app/logs \
&& chown -R admin:devops /app

COPY --chown=admin:devops --from=build /app/target/*.jar /app/app.jar

EXPOSE 8095

USER admin

ENV JAVA_TOOL_OPTIONS="-Djava.io.tmpdir=/app/tmp"

ENTRYPOINT ["java","-jar","/app/app.jar"]


