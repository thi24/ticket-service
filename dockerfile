FROM maven:3.9.6-amazoncorretto-21-debian as build-stage

COPY src /home/app/src
COPY pom.xml /home/app

RUN mvn -f /home/app/pom.xml clean package

FROM registry.access.redhat.com/ubi8/openjdk-21:1.19-1
ENV LANGUAGE='en_US:en'
ARG DATABASE_USERNAME
ARG DATABASE_PASSWORD
ARG KEYCLOAK_SECRET
ARG EVENT_SERVICE_BASE_URL
ENV DATABASE_USERNAME $DATABASE_USERNAME
ENV DATABASE_PASSWORD $DATABASE_PASSWORD
ENV KEYCLOAK_SECRET $KEYCLOAK_SECRET
ENV EVENT_SERVICE_BASE_URL $EVENT_SERVICE_BASE_URL

USER jboss
COPY --from=build-stage /home/app/target/quarkus-app/lib/ /deployments/lib/
COPY --from=build-stage /home/app/target/quarkus-app/*.jar /deployments/
COPY --from=build-stage /home/app/target/quarkus-app/app/ /deployments/app/
COPY --from=build-stage /home/app/target/quarkus-app/quarkus/ /deployments/quarkus/

EXPOSE 8081
USER 185
ENV AB_JOLOKIA_OFF=""
ENV JAVA_OPTS="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
ENV JAVA_APP_JAR="/deployments/quarkus-run.jar"