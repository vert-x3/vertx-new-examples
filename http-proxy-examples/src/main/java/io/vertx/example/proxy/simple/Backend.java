package io.vertx.example.proxy.simple;

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
      .requestHandler(req -> {
        req.response().putHeader("content-type", "text/html").end("<html><body><h1>Hello from Vert.x!</h1></body></html>");
      }).listen(7070);
  }
}
