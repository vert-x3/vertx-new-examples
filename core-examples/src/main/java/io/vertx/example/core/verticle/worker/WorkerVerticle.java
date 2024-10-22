package io.vertx.example.core.verticle.worker;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;

/**
 * An example of worker verticle
 */
public class WorkerVerticle extends VerticleBase {


  @Override
  public Future<?> start() throws Exception {
    System.out.println("[Worker] Starting in " + Thread.currentThread().getName());

    vertx.eventBus().<String>consumer("sample.data", message -> {
      System.out.println("[Worker] Consuming data in " + Thread.currentThread().getName());
      String body = message.body();
      message.reply(body.toUpperCase());
    });

    return super.start();
  }
}
