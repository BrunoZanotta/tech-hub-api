FROM gradle:8.14.3-jdk21 AS build
WORKDIR /app
COPY settings.gradle* build.gradle* gradlew ./
COPY gradle ./gradle
RUN chmod +x gradlew
RUN ./gradlew --no-daemon dependencies || true
COPY . .
RUN ./gradlew --no-daemon clean bootJar -x test || ./gradlew --no-daemon clean build -x test

FROM eclipse-temurin:21-jre
ENV PORT=8080
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
CMD ["sh","-c","java -jar app.jar --server.port=${PORT}"]