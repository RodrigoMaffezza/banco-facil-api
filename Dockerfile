# Etapa 1: build - imagem completa com Maven + JDK, usada apenas para compilar.
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /build

COPY pom.xml .
COPY src ./src

RUN mvn --batch-mode -DskipTests package

# Etapa 2: runtime - imagem minima, somente com o JRE necessario para rodar o jar.
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

RUN addgroup -S app && adduser -S app -G app

COPY --from=build /build/target/banco-facil-api-0.0.1-SNAPSHOT.jar app.jar

USER app

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
