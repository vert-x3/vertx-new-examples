package io.vertx.example.proxy.interception;

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
        if (req.path().equals("/app") || req.path().startsWith("/app")) {
          req.response()
            .putHeader("x-internal-header", "some-internal-header-value")
            .putHeader("content-type", "text/html")
            .end("<html><body><h1>Hello from Vert.x!</h1></body></html>");
        } else {
          req.response().setStatusCode(400).end();
        }
      }).listen(7070);
  }
}
