package io.vertx.example.healthcheck.web;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.healthchecks.HealthChecks;
import io.vertx.ext.healthchecks.Status;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.healthchecks.HealthCheckHandler;
import io.vertx.launcher.application.VertxApplication;

public class Server extends VerticleBase {

  public static void main(String[] args) {
    VertxApplication.main(new String[]{Server.class.getName()});
  }

  @Override
  public Future<?> start() {
    Router router = Router.router(vertx);

    HealthChecks hc = HealthChecks.create(vertx);

    hc.register("my-procedure-name", promise -> {
      promise.complete(Status.OK(new JsonObject().put("available-memory", "2mb")));
    });

    hc.register("my-second-procedure-name", promise -> {
      promise.complete(Status.KO(new JsonObject().put("load", 99)));
    });

    router.get("/health").handler(HealthCheckHandler.createWithHealthChecks(hc));

    return vertx.createHttpServer()
      .requestHandler(router)
      .listen(8080);
  }
}
