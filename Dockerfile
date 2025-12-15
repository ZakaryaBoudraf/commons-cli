# Multi-stage build for JMH benchmarks
# Stage 1: Build the project
FROM maven:3.9.6-eclipse-temurin-21 as builder

# Set working directory
WORKDIR /app

# Copy Maven files first for better layer caching
COPY pom.xml ./

# Download dependencies
RUN mvn dependency:resolve dependency:resolve-sources -q

# Copy source code
COPY src ./src

# Build the project with benchmarks profile
RUN mvn clean package -Pbenchmarks -DskipTests -Drat.skip=true -Dcheckstyle.skip=true -Dspotbugs.skip=true -q

# Stage 2: Runtime image
FROM eclipse-temurin:21-jre

WORKDIR /benchmarks

# Copy the benchmarks jar from builder stage
COPY --from=builder /app/target/benchmarks.jar ./benchmarks.jar

# Set JVM options for better benchmark performance
ENV JAVA_OPTS="-XX:+UseG1GC -XX:+UnlockExperimentalVMOptions"

# Create entrypoint script for easier benchmark execution
RUN echo '#!/bin/bash\njava $JAVA_OPTS -jar benchmarks.jar "$@"' > /usr/local/bin/run-benchmarks.sh && \
    chmod +x /usr/local/bin/run-benchmarks.sh

# Set entrypoint to our script
ENTRYPOINT ["/usr/local/bin/run-benchmarks.sh"]

# Default: run all benchmarks with reasonable settings
CMD ["-wi", "5", "-i", "5", "-f", "1"]