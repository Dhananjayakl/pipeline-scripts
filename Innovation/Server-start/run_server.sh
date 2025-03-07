#!/bin/bash

export OTEL_EXPORTER_OTLP_ENDPOINT=http://192.168.1.229:4317
java -javaagent:/HRMS/opentelemetry-javaagent.jar \
     -Dotel.service.name=SERVER-BACKEND \
     -Dotel.traces.exporter=otlp \
     -Dotel.metrics.exporter=otlp \
     -Dotel.logs.exporter=otlp \
     -Dotel.exporter.otlp.protocol=grpc \
     -Dotel.resource.attributes=service.name=HRMS,service.version=1.0 \
     -jar /HRMS/SERVER.jar