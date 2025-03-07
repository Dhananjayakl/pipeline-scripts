#!/bin/bash

CONTAINER_NAME="otel-collector"

# Check if the container is running
if docker ps -q -f name="$CONTAINER_NAME" | grep -q .; then
  echo "Stopping and removing existing container: $CONTAINER_NAME..."
  docker stop "$CONTAINER_NAME" && docker rm "$CONTAINER_NAME"
fi

# Run the container
echo "Starting OpenTelemetry Collector..."
docker run -d \
  -p 4317:4317 \
  -p 4318:4318 \
  -p 9464:9464 \
  -p 8888:8888 \
  -p 1888:1888 \
  -p 8889:8889 \
  -p 13133:13133 \
  -p 55679:55679 \
  -v /otel/config.yaml:/etc/otelcol-contrib/config.yaml \
  --name "$CONTAINER_NAME" \
  otel/opentelemetry-collector-contrib

# Verify if the container started successfully
if docker ps -q -f name="$CONTAINER_NAME" | grep -q .; then
  echo "OpenTelemetry Collector started successfully!"
else
  echo "Failed to start OpenTelemetry Collector. Check logs for details."
fi
