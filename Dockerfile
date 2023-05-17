FROM openjdk:17-alpine3.14
COPY target/*.jar /recommendation.jar
ENTRYPOINT ["java","-jar","/recommendation.jar"]