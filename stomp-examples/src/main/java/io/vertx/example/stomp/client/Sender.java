package io.vertx.example.stomp.client;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.stomp.StompClient;
import io.vertx.ext.stomp.StompClientOptions;
import io.vertx.launcher.application.VertxApplication;

public class Sender extends VerticleBase {

  public static void main(String[] args) {
    VertxApplication.main(new String[]{Sender.class.getName()});
  }

  @Override
  public Future<?> start() throws Exception {
    return StompClient.create(vertx, new StompClientOptions().setLogin("artemis").setPasscode("artemis"))
      .connect(61613, "localhost")
      .onSuccess(connection -> {
        vertx.setPeriodic(3000, l -> {
          connection.send("/queue", Buffer.buffer("Hello World"));
        });
      });
  }
}
