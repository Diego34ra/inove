# Stage 1: Build do Maven
FROM maven:3.9.3-eclipse-temurin-17 AS build
WORKDIR /app

# cache de dependências
COPY pom.xml .
RUN mvn -q -B dependency:go-offline

# código
COPY src ./src
RUN mvn -q clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

# perfil padrão pode ser alterado via ENV
ENV SPRING_PROFILES_ACTIVE=prod

EXPOSE 8080

# Binda na porta do provedor ($PORT) com fallback local 8080
ENTRYPOINT ["sh","-c","java -Dserver.port=${PORT:-8080} -jar app.jar --spring.profiles.active=${SPRING_PROFILES_ACTIVE}"]
