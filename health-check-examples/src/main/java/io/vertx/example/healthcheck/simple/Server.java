package io.vertx.example.healthcheck.simple;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.healthchecks.HealthChecks;
import io.vertx.ext.healthchecks.Status;
import io.vertx.launcher.application.VertxApplication;

public class Server extends AbstractVerticle {

  public static void main(String[] args) {
    VertxApplication.main(new String[]{Server.class.getName()});
  }

  @Override
  public void start() {
    HealthChecks hc = HealthChecks.create(vertx);

    hc.register("my-procedure-name", promise -> {
      promise.complete(Status.OK(new JsonObject().put("available-memory", "2mb")));
    });

    hc.register("my-second-procedure-name", promise -> {
      promise.complete(Status.KO(new JsonObject().put("load", 99)));
    });

    hc.checkStatus().onComplete(checkResult -> {
      System.out.println("checkResult = " + checkResult.toJson().encodePrettily());
    }, Throwable::printStackTrace);
  }
}
