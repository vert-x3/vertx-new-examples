package io.vertx.example.core.http2.simple;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
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
          .setKeyPath("io/vertx/example/core/http2/simple/server-key.pem")
          .setCertPath("io/vertx/example/core/http2/simple/server-cert.pem")
        ));

    return server
      .requestHandler(req -> {
        req.response().putHeader("content-type", "text/html").end("<html><body>" +
          "<h1>Hello from vert.x!</h1>" +
          "<p>version = " + req.version() + "</p>" +
          "</body></html>");
      })
      .listen(8443);
  }
}
