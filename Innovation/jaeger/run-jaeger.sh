#!/bin/bash

# Set Jaeger container name and image version
JAEGER_CONTAINER_NAME="jaeger"
JAEGER_IMAGE="jaegertracing/jaeger:2.3.0"
CONFIG_DIR="/jaeger/"

# Ensure the configuration directory exists
if [ ! -d "$CONFIG_DIR" ]; then
    echo "Creating configuration directory: $CONFIG_DIR"
    mkdir -p "$CONFIG_DIR"
fi

# Check if the container is already running
if docker ps -a --format '{{.Names}}' | grep -q "^${JAEGER_CONTAINER_NAME}$"; then
    echo "Jaeger container already exists. Stopping and removing..."
    docker stop $JAEGER_CONTAINER_NAME && docker rm $JAEGER_CONTAINER_NAME
fi

# Run Jaeger container
echo "Starting Jaeger container..."
docker run -d --name $JAEGER_CONTAINER_NAME \
  --restart unless-stopped \
  -e GRPC_MAX_FRAME_SIZE=16777216 \
  -p 16686:16686 \
  -p 14317:4317 \
  -p 14318:4318 \
  -p 5778:5778 \
  -p 9411:9411 \
  -e COLLECTOR_OTLP_ENABLED=true \
  $JAEGER_IMAGE

# Check if the container started successfully
if [ $? -eq 0 ]; then
    echo "✅ Jaeger container started successfully."
    echo "🌐 Web UI available at: http://192.168.1.229:16686"
else
    echo "❌ Failed to start Jaeger container."
    exit 1
fi
