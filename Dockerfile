# Use an official Maven image with Java 11
FROM maven:3.9.6-eclipse-temurin-11

# Set the working directory inside the container
WORKDIR /app

# Copy all your project files into the container
COPY . .

# Compile the project inside the Docker container
# We skip the strict checks (Rat, Checkstyle) to ensure a smooth build
RUN mvn clean test-compile -DskipTests -Drat.skip=true -Dcheckstyle.skip=true -Dspotbugs.skip=true

# The default command to run when the container starts
# This runs your ManualBenchmark, exactly like we did locally
CMD ["mvn", "exec:java", "-Dexec.mainClass=org.apache.commons.cli.ManualBenchmark", "-Dexec.classpathScope=test"]