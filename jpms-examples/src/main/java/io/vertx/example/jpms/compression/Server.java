package io.vertx.example.jpms.compression;

import io.netty.handler.codec.compression.StandardCompressionOptions;
import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;

public class Server extends VerticleBase {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new Server())
      .onFailure(Throwable::printStackTrace);
  }

  @Override
  public Future<?> start() {

    HttpServer server = vertx.createHttpServer(new HttpServerOptions()
      .setCompressionSupported(true)
      .addCompressor(StandardCompressionOptions.brotli()));

    server.requestHandler(req -> {
      req.response().end(recursiveHelloWorld(10)
        .put("http", req.version())
        .toString());
    });

    return server.listen(8080);
  }

  // Generate a large response to be compressed
  private static JsonObject recursiveHelloWorld(int depth) {
    JsonObject json = new JsonObject();
    for (int i = 0;i < 16;i++) {
      json.put("msg-" + i, "Hello World " + i);
    }
    if (depth > 0) {
      json.put("nested", recursiveHelloWorld(depth - 1));
    }
    return json;
  }

}
