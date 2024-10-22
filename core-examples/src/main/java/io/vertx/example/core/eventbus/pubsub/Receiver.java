package io.vertx.example.core.eventbus.pubsub;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.eventbus.EventBus;
import io.vertx.launcher.application.VertxApplication;

/*
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class Receiver extends VerticleBase {

  public static void main(String[] args) {
    VertxApplication.main(new String[]{Receiver.class.getName(), "-cluster"});
  }


  @Override
  public Future<?> start() throws Exception {

    EventBus eb = vertx.eventBus();

    eb.consumer("news-feed", message -> System.out.println("Received news on consumer 1: " + message.body()));

    eb.consumer("news-feed", message -> System.out.println("Received news on consumer 2: " + message.body()));

    eb.consumer("news-feed", message -> System.out.println("Received news on consumer 3: " + message.body()));

    System.out.println("Ready!");

    return super.start();
  }
}
