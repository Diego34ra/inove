# Stage 1: Build do Maven
FROM maven:3.9.3-eclipse-temurin-17 AS build

WORKDIR /app

# Copia apenas o pom.xml primeiro para cache de dependências
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copia todo o código-fonte
COPY src ./src

# Build do jar, sem testes
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copia o jar gerado do stage 1
COPY --from=build /app/target/*.jar app.jar

# Variáveis de ambiente padrão (profile do Docker e segredos podem ser sobrescritos no docker-compose)
ENV SPRING_PROFILES_ACTIVE=docker
ENV JWT_SECRET=my-secret-key
ENV AWS_ACCESS_KEY=
ENV AWS_SECRET_KEY=
ENV MAIL_PASSWORD=

EXPOSE 8080

# Comando de execução com profile ativo
ENTRYPOINT ["sh", "-c", "java -jar app.jar --spring.profiles.active=${SPRING_PROFILES_ACTIVE}"]
