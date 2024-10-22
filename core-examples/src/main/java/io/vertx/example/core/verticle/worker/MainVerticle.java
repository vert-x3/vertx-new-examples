package io.vertx.example.core.verticle.worker;

import io.vertx.core.*;
import io.vertx.launcher.application.VertxApplication;

/**
 * An example illustrating how worker verticles can be deployed and how to interact with them.
 *
 * This example prints the name of the current thread at various locations to exhibit the event loop <-> worker
 * thread switches.
 */
public class MainVerticle extends VerticleBase {

  public static void main(String[] args) {
    VertxApplication.main(new String[]{MainVerticle.class.getName()});
  }

  @Override
  public Future<?> start() throws Exception {
    System.out.println("[Main] Running in " + Thread.currentThread().getName());
    vertx
      .deployVerticle("io.vertx.example.core.verticle.worker.WorkerVerticle",
        new DeploymentOptions().setThreadingModel(ThreadingModel.WORKER));

    vertx.eventBus().request(
      "sample.data",
      "hello vert.x").onComplete(r -> {
        System.out.println("[Main] Receiving reply ' " + r.result().body()
          + "' in " + Thread.currentThread().getName());
      }
    );

    return super.start();
  }
}
