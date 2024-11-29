package io.vertx.example.otel;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.sdk.resources.Resource;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.example.tracing.GatewayVerticle;
import io.vertx.tracing.opentelemetry.OpenTelemetryOptions;

public class Gateway {

  public static void main(String[] args) {
    Resource resource = Resource.getDefault().toBuilder()
      .put(AttributeKey.stringKey("service.name"), "gateway")
      .build();
    OpenTelemetry otel = OpenTelemetryConfig.configure(resource);
    Vertx vertx = Vertx.vertx(new VertxOptions().setTracingOptions(new OpenTelemetryOptions(otel)));
    vertx.deployVerticle(new GatewayVerticle()).await();
  }
}
