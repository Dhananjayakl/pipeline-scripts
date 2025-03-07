import { WebTracerProvider } from "@opentelemetry/sdk-trace-web";
import {
  ConsoleSpanExporter,
  SimpleSpanProcessor,
} from "@opentelemetry/sdk-trace-base";
import { OTLPTraceExporter } from "@opentelemetry/exporter-trace-otlp-http";
import { Resource } from "@opentelemetry/resources";
import { SemanticResourceAttributes } from "@opentelemetry/semantic-conventions";
import { trace, context } from "@opentelemetry/api";
import { useEffect } from "react";
import { useLocation } from "react-router-dom";
import { registerInstrumentations } from "@opentelemetry/instrumentation";
import { FetchInstrumentation } from "@opentelemetry/instrumentation-fetch";
import { XMLHttpRequestInstrumentation } from "@opentelemetry/instrumentation-xml-http-request";
import { DocumentLoadInstrumentation } from "@opentelemetry/instrumentation-document-load";
import {
  MeterProvider,
  PeriodicExportingMetricReader,
} from "@opentelemetry/sdk-metrics";
import { OTLPMetricExporter } from "@opentelemetry/exporter-metrics-otlp-http";

// ✅ Step 1: Tracing Setup (No changes here)
const provider = new WebTracerProvider({
  resource: new Resource({
    [SemanticResourceAttributes.SERVICE_NAME]: "react-frontend",
  }),
});
const traceExporter = new OTLPTraceExporter({
  url: "http://192.168.1.229:4318/v1/traces",
});
provider.addSpanProcessor(new SimpleSpanProcessor(traceExporter));
provider.addSpanProcessor(new SimpleSpanProcessor(new ConsoleSpanExporter()));
provider.register();

// ✅ Step 2: Metrics Setup
const metricExporter = new OTLPMetricExporter({
  url: "http://192.168.1.229:4318/v1/metrics", // Make sure this endpoint is set up in your OTEL Collector
  headers: {}, // Add any necessary headers if required
});
const meterProvider = new MeterProvider({
  resource: new Resource({
    [SemanticResourceAttributes.SERVICE_NAME]: "react-frontend",
  }),
  readers: [
    new PeriodicExportingMetricReader({
      exporter: metricExporter,
      exportIntervalMillis: 10000,
    }),
  ],
});
const meter = meterProvider.getMeter("frontend-metrics");

// ✅ Step 3: Web Vitals (Page Load Time, etc.)
const pageLoadMetric = meter.createHistogram("frontend.page_load_time", {
  description: "Time taken to load the page",
});
window.addEventListener("load", () => {
  const loadTime =
    performance.timing.domContentLoadedEventEnd -
    performance.timing.navigationStart;
  pageLoadMetric.record(loadTime);
});

// ✅ Step 4: API Call Metrics
const apiCallDuration = meter.createHistogram("frontend.api_call_duration", {
  description: "Duration of API calls",
});
registerInstrumentations({
  instrumentations: [
    new FetchInstrumentation({
      propagateTraceHeaderCorsUrls: [/.*/],
      applyCustomAttributesOnSpan(span, request, response) {
        span.setAttribute("http.url", request.url);
        span.setAttribute("http.method", request.method);
        response?.time && apiCallDuration.record(response.time);
      },
    }),
    new XMLHttpRequestInstrumentation(),
    new DocumentLoadInstrumentation(),
  ],
});

// ✅ Step 5: React Router Tracing
export const tracer = trace.getTracer("react-router-tracer");
export function useNavigationTracing() {
  const location = useLocation();
  useEffect(() => {
    const span = tracer.startSpan(`Navigating to ${location.pathname}`);
    span.end();
  }, [location]);
}

console.log(
  "OpenTelemetry initialized for tracing and metrics collection in React frontend."
);
