package io.vertx.example.stomp.client;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.ext.stomp.StompClient;
import io.vertx.ext.stomp.StompClientOptions;
import io.vertx.launcher.application.VertxApplication;

public class Receiver extends VerticleBase {

  public static void main(String[] args) {
    VertxApplication.main(new String[]{Receiver.class.getName()});
  }

  @Override
  public Future<?> start() throws Exception {
    return StompClient.create(vertx, new StompClientOptions().setLogin("artemis").setPasscode("artemis"))
      .connect(61613, "localhost")
      .onSuccess(connection -> {
        connection.subscribe("/queue", frame ->
          System.out.println("Just received a message from /queue : " + frame.getBodyAsString()));
      });
  }
}
