package io.vertx.example.proxy.websocket;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.launcher.application.VertxApplication;

public class Backend extends VerticleBase {

  public static void main(String[] args) {
    VertxApplication.main(new String[]{Backend.class.getName()});
  }

  @Override
  public Future<?> start() {
    return vertx
      .createHttpServer()
      .webSocketHandler(ws -> {
        vertx.setPeriodic(3000, tid -> {
          if (ws.isClosed()) {
            vertx.cancelTimer(tid);
            return;
          }
          ws.writeTextMessage("Hello World");
        });
      }).listen(7070);
  }
}
