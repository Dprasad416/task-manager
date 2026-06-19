FROM maven:3.9.16-eclipse-temurin-17-alpine AS build
ADD . /task
WORKDIR /task
RUN mvn clean install

FROM maven:3.9.16-eclipse-temurin-17-alpine AS runtime
WORKDIR /app
COPY --from=build /task/target/*.jar /app/
EXPOSE 8080