import { WebTracerProvider } from "@opentelemetry/sdk-trace-web";
import {
  ConsoleSpanExporter,
  SimpleSpanProcessor,
} from "@opentelemetry/sdk-trace-base";
import { OTLPTraceExporter } from "@opentelemetry/exporter-trace-otlp-http";
import { registerInstrumentations } from "@opentelemetry/instrumentation";
import { FetchInstrumentation } from "@opentelemetry/instrumentation-fetch";
import { XMLHttpRequestInstrumentation } from "@opentelemetry/instrumentation-xml-http-request";
import { DocumentLoadInstrumentation } from "@opentelemetry/instrumentation-document-load";
import { Resource } from "@opentelemetry/resources";
import { SemanticResourceAttributes } from "@opentelemetry/semantic-conventions";
import { trace } from "@opentelemetry/api";
import { useEffect } from "react";
import { useLocation } from "react-router-dom";

// Set up the trace provider with resource attributes (service name)
const provider = new WebTracerProvider({
  resource: new Resource({
    [SemanticResourceAttributes.SERVICE_NAME]: "react-frontend", // Replace with your app's name
  }),
});

// Configure the OTLP HTTP exporter (replace with your OpenTelemetry Collector URL)
const exporter = new OTLPTraceExporter({
  url: "http://192.168.1.229:4318/v1/traces", // Make sure this is the correct OTEL collector URL
});

// Add span processors
provider.addSpanProcessor(new SimpleSpanProcessor(exporter)); // Send traces to OTLP exporter
provider.addSpanProcessor(new SimpleSpanProcessor(new ConsoleSpanExporter())); // Logs traces to console

// Register the provider
provider.register();

// Register instrumentations for automatic tracing of Fetch, XHR, and Document Load
registerInstrumentations({
  instrumentations: [
    new FetchInstrumentation(), // Instrument fetch calls
    new XMLHttpRequestInstrumentation(), // Instrument XHR calls
    new DocumentLoadInstrumentation(), // Instrument page load
  ],
});

// Export Tracer
export const tracer = trace.getTracer("react-router-tracer");

// Custom hook for React Router tracing
export function useNavigationTracing() {
  const location = useLocation();

  useEffect(() => {
    const span = tracer.startSpan(`Navigating to ${location.pathname}`);
    span.end();
  }, [location]);
}

console.log(
  "OpenTelemetry initialized for React frontend with manual React Router tracing."
);
