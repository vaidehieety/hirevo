# hirevo/Dockerfile — the recipe for packing Hirevo into a container

# ---------- Stage 1: the KITCHEN — build the JAR ----------
# A full Java Development Kit (has the compiler, can run Maven)
FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace

# Copy the Maven wrapper + pom.xml FIRST and download all libraries.
# Docker remembers this step, so later builds skip the download if pom.xml didn't change.
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw -q dependency:go-offline

# Now copy your code and build the JAR
# (tests already ran on your Mac; from v1.0 they also run automatically on GitHub)
COPY src ./src
RUN ./mvnw -q package -DskipTests

# ---------- Stage 2: the TIFFIN BOX — just what's needed to RUN ----------
# Only a Java Runtime: no compiler, no Maven, so it's smaller and safer
FROM eclipse-temurin:21-jre
WORKDIR /app

# Create a normal user and run as them, never as the all-powerful "root" (safety)
RUN useradd --system --uid 1001 spring
COPY --from=build /workspace/target/*.jar app.jar
USER spring

# The app listens on a port (documentation for humans/tools)
EXPOSE 8080
# Start the app. Let Java use up to 75% of the container's memory.
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]