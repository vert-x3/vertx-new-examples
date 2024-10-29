package io.vertx.example.jpms.openssl;

import io.vertx.core.*;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.JksOptions;
import io.vertx.core.net.OpenSSLEngineOptions;

public class Server extends VerticleBase {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new Server())
      .onFailure(Throwable::printStackTrace);
  }

  @Override
  public Future<?> start() {
    HttpServerOptions options = new HttpServerOptions()
      .setSslEngineOptions(new OpenSSLEngineOptions())
      .setKeyCertOptions(new JksOptions()
        .setPath("server-keystore.jks")
        .setPassword("wibble"))
      .setSsl(true);

    HttpServer server = vertx
      .createHttpServer(options)
      .requestHandler(req -> {
        req.response().end(new JsonObject()
          .put("http", req.version())
          .put("message", "Hello World")
          .put("nativeTransport", vertx.isNativeTransportEnabled())
          .toString());
      });

    return server.listen(8443);
  }
}
