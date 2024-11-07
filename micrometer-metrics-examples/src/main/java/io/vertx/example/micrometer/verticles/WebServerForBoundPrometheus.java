package io.vertx.example.micrometer.verticles;

import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.ext.web.Router;
import io.vertx.micrometer.backends.BackendRegistries;

/**
 * @author Joel Takvorian, jtakvori@redhat.com
 */
public class WebServerForBoundPrometheus extends VerticleBase {

  @Override
  public Future<?> start() {
    Router router = Router.router(vertx);
    PrometheusMeterRegistry registry = (PrometheusMeterRegistry) BackendRegistries.getDefaultNow();
    // Setup a route for metrics
    router.route("/metrics").handler(ctx -> {
      String response = registry.scrape();
      ctx.response().end(response);
    });
    router.get("/").handler(ctx -> {
      Greetings.get(vertx).onComplete(greetingResult -> ctx.response().end(greetingResult.result()));
    });
    return vertx
      .createHttpServer()
      .requestHandler(router)
      .listen(8080);
  }
}
