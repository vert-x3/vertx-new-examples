package io.vertx.example.micrometer.verticles;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;

/**
 * @author Joel Takvorian, jtakvori@redhat.com
 */
public class EventbusConsumer extends VerticleBase {
  @Override
  public Future<?> start() {
    return vertx
      .eventBus()
      .<String>consumer("greeting", message -> {
        String greeting = message.body();
        System.out.println("Received: " + greeting);
        Greetings.get(vertx)
          .onComplete(greetingResult -> message.reply(greetingResult.result()));
      })
      .completion();
  }
}
