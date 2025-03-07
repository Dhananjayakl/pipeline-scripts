#experimental not working
#!/bin/bash

# Set OpenTelemetry environment variables
export OTEL_EXPORTER_OTLP_ENDPOINT="http://192.168.1.229:4317"  # Ensure this matches your Otel Collector
export OTEL_SERVICE_NAME="frontend-app"
export OTEL_RESOURCE_ATTRIBUTES="deployment.environment=production"

# Set log, trace, and metric levels
export OTEL_LOG_LEVEL=debug
export OTEL_TRACES_EXPORTER=otlp
export OTEL_METRICS_EXPORTER=otlp
export OTEL_TRACES_SAMPLER=parentbased_always_on

# Set the OTLP protocol (HTTP/Protobuf or gRPC)
export OTEL_EXPORTER_OTLP_PROTOCOL="grpc"

# Enable Auto-Instrumentation for Node.js
NODE_OPTIONS="--require @opentelemetry/auto-instrumentations-node/register" npm start -- --host
