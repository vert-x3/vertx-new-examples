package io.vertx.example.jpms.http;

import io.vertx.core.*;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;

public class Server extends VerticleBase {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new Server())
      .onFailure(Throwable::printStackTrace);
  }

  @Override
  public Future<?> start() {

    HttpServer server = vertx.createHttpServer();

    server.requestHandler(req -> {
      req.response().end(new JsonObject()
        .put("http", req.version())
        .put("message", "Hello World")
        .toString());
    });

    return server.listen(8080);
  }
}
