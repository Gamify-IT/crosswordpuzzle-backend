#
# Build stage
#
FROM maven:3.6.3-openjdk-17-slim AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package


#
# Package stage
#
FROM openjdk:17-jdk-slim
COPY --from=build /home/app/target/crossword-service-0.0.1-SNAPSHOT.jar /usr/local/lib/crossword-service.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/local/lib/crossword-service.jar", "--spring.datasource.url=jdbc:postgresql://host.docker.internal:5432/crosswordpuzzle"]