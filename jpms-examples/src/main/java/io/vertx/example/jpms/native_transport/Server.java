package io.vertx.example.jpms.native_transport;

import io.vertx.core.*;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.JksOptions;

public class Server extends VerticleBase {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx(new VertxOptions()
      .setPreferNativeTransport(true));
    if (!vertx.isNativeTransportEnabled()) {
      throw new RuntimeException("Add your OS/arch specific modules (explained in README)");
    }
    vertx.deployVerticle(new Server())
      .onFailure(Throwable::printStackTrace);
  }

  @Override
  public Future<?> start() {
    HttpServer server = vertx
      .createHttpServer()
      .requestHandler(req -> {
        req.response().end(new JsonObject()
          .put("http", req.version())
          .put("message", "Hello World")
          .put("nativeTransport", vertx.isNativeTransportEnabled())
          .toString());
      });

    return server.listen(8080);
  }
}
