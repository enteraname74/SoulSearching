FROM eclipse-temurin:17-jdk-jammy AS builder

RUN apt-get update \
    && apt-get install -y --no-install-recommends libatomic1 \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /project

COPY . .

RUN chmod +x gradlew
RUN ./gradlew :app:wasmJsBrowserDistribution --no-daemon


FROM nginx:1.29-alpine

COPY nginx/web.conf /etc/nginx/conf.d/default.conf

COPY --from=builder \
    /project/app/build/dist/wasmJs/productionExecutable/ \
    /usr/share/nginx/html/

EXPOSE 80