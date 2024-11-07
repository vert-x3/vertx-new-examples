package io.vertx.example.circuit.breaker;

import io.vertx.circuitbreaker.CircuitBreaker;
import io.vertx.circuitbreaker.CircuitBreakerOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClientAgent;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpResponseExpectation;
import io.vertx.launcher.application.VertxApplication;

/**
 * @author <a href="pahan.224@gmail.com">Pahan</a>
 */

public class Client extends VerticleBase {

  public static void main(String[] args) {
    VertxApplication.main(new String[]{Client.class.getName()});
  }

  private HttpClientAgent httpClient;
  private CircuitBreaker breaker;

  @Override
  public Future<?> start() {
    CircuitBreakerOptions options = new CircuitBreakerOptions()
      .setMaxFailures(5)
      .setTimeout(5000)
      .setFallbackOnFailure(true);

    httpClient = vertx.createHttpClient();
    breaker =
      CircuitBreaker.create("my-circuit-breaker", vertx, options)
        .openHandler(v -> {
          System.out.println("Circuit opened");
        }).closeHandler(v -> {
          System.out.println("Circuit closed");
        });

    return breaker
      .executeWithFallback(() -> httpClient
        .request(HttpMethod.GET, 8080, "localhost", "/").
        compose(req -> req
          .send()
          .expecting(HttpResponseExpectation.SC_OK)
          .compose(resp -> resp.body())
          .map(body -> body.toString())), v -> {
        // Executed when the circuit is opened
        return "Hello (fallback)";
      })
      .onSuccess(res -> {
        // Do something with the result
        System.out.println("Result: " + res);
      });
  }
}
