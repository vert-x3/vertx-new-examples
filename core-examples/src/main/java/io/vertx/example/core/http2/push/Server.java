package io.vertx.example.core.http2.push;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.net.PemKeyCertOptions;
import io.vertx.launcher.application.VertxApplication;

/*
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class Server extends VerticleBase {

  public static void main(String[] args) {
    VertxApplication.main(new String[]{Server.class.getName()});
  }

  @Override
  public Future<?> start() throws Exception {

    HttpServer server =
      vertx.createHttpServer(new HttpServerOptions()
        .setUseAlpn(true)
        .setSsl(true)
        .setKeyCertOptions(new PemKeyCertOptions()
          .setKeyPath("io/vertx/example/core/http2/push/server-key.pem")
          .setCertPath("io/vertx/example/core/http2/push/server-cert.pem")
        ));

    server.requestHandler(req -> {
      String path = req.path();
      HttpServerResponse resp = req.response();
      if ("/".equals(path)) {
        resp.push(HttpMethod.GET, "/script.js").onComplete(ar -> {
          if (ar.succeeded()) {
            System.out.println("sending push");
            HttpServerResponse pushedResp = ar.result();
            pushedResp.sendFile("io/vertx/example/core/http2/push/script.js");
          } else {
            // Sometimes Safari forbids push : "Server push not allowed to opposite endpoint."
          }
        });

        resp.sendFile("io/vertx/example/core/http2/push/index.html");
      } else if ("/script.js".equals(path)) {
        resp.sendFile("io/vertx/example/core/http2/push/script.js");
      } else {
        System.out.println("Not found " + path);
        resp.setStatusCode(404).end();
      }
    });

    return server
      .listen(8443, "localhost")
      .onSuccess(ar -> System.out.println("Server started"));
  }
}
