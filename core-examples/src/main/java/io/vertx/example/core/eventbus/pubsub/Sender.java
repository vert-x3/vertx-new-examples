package io.vertx.example.core.eventbus.pubsub;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.eventbus.EventBus;
import io.vertx.launcher.application.VertxApplication;

/*
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class Sender extends VerticleBase {

  public static void main(String[] args) {
    VertxApplication.main(new String[]{Sender.class.getName(), "-cluster"});
  }

  @Override
  public Future<?> start() throws Exception {

    EventBus eb = vertx.eventBus();

    // Send a message every second

    vertx.setPeriodic(1000, v -> eb.publish("news-feed", "Some news!"));

    return super.start();
  }
}
