package io.vertx.example.micrometer.verticles;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;

/**
 * @author Joel Takvorian, jtakvori@redhat.com
 */
public class EventbusProducer extends VerticleBase {

  @Override
  public Future<?> start() throws Exception {
    vertx.setPeriodic(1000, x -> {
      Greetings.get(vertx)
        .onComplete(greetingResult -> vertx.eventBus().send("greeting", greetingResult.result()));
    });
    return super.start();
  }
}
