package io.vertx.example.circuit.breaker;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.launcher.application.VertxApplication;

public class Server extends VerticleBase {

  public static void main(String[] args) {
    VertxApplication.main(new String[]{Server.class.getName()});
  }

  @Override
  public Future<?> start() {
    return vertx
      .createHttpServer()
    .requestHandler(req -> req.response().end("Bonjour"))
    .listen(8080);
  }

}
