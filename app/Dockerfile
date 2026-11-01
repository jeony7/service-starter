FROM node:24-alpine AS frontend
WORKDIR /workspace/frontend
COPY frontend/package.json frontend/package-lock.json ./
RUN npm ci
COPY frontend/ ./
RUN npm run build

FROM eclipse-temurin:21-jdk AS backend
WORKDIR /workspace/backend
COPY backend/ ./
COPY --from=frontend /workspace/frontend/dist /workspace/frontend/dist
RUN chmod +x gradlew && ./gradlew --no-daemon bootJar

FROM eclipse-temurin:21-jre
WORKDIR /app
RUN groupadd --system app && useradd --system --gid app app \
    && mkdir -p /app/data && chown -R app:app /app
COPY --from=backend --chown=app:app /workspace/backend/build/libs/app.jar ./app.jar
ENV APP_DATA_DIR=/app/data SERVER_ADDRESS=0.0.0.0 SERVER_PORT=3000
USER app
EXPOSE 3000
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
