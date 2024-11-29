package io.vertx.example.otel;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.sdk.resources.Resource;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.example.tracing.ChuckNorrisJokesVerticle;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.tracing.opentelemetry.OpenTelemetryOptions;
import org.testcontainers.containers.GenericContainer;

public class JokeService {

  public static void main(String[] args) {
    GenericContainer<?> postgres = new GenericContainer<>("postgres:17")
      .withEnv("POSTGRES_PASSWORD", "postgres")
      .withExposedPorts(5432);
    postgres.start();

    PgConnectOptions options = new PgConnectOptions()
      .setPort(postgres.getMappedPort(5432))
      .setHost(postgres.getHost())
      .setDatabase("postgres")
      .setUser("postgres")
      .setPassword("postgres");

    Resource resource = Resource.getDefault().toBuilder()
      .put(AttributeKey.stringKey("service.name"), "joke-service")
      .build();
    OpenTelemetry otel = OpenTelemetryConfig.configure(resource);
    Vertx vertx = Vertx.vertx(new VertxOptions().setTracingOptions(new OpenTelemetryOptions(otel)));
    vertx.deployVerticle(new ChuckNorrisJokesVerticle(options)).await();
    System.out.println("JokeService started");
  }
}
