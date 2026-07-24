# =============================================================
# Build multi-stage. O provedor NÃO deve tentar detectar o runtime sozinho:
# Java 25 + Spring Boot 4 são recentes e os buildpacks automáticos ficam atrás.
# =============================================================

FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app
# Camada de dependências separada: só refaz o download quando o pom muda.
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src ./src
RUN mvn -B -q -DskipTests package

FROM eclipse-temurin:25-jre
WORKDIR /app

# Usuário sem privilégios
RUN useradd --system --create-home --shell /usr/sbin/nologin odonto
USER odonto

COPY --from=build /app/target/*.jar app.jar

# Ajustes para o container de 512 MB do free tier.
# Sem TieredStopAtLevel=1: melhoraria o cold start mas limitaria o JIT em regime.
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=70 -XX:+UseSerialGC -Xss512k"
ENV SPRING_PROFILES_ACTIVE=prod

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
