package io.vertx.example.core.http2.customframes;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.buffer.Buffer;
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
      vertx.createHttpServer(new HttpServerOptions().
        setUseAlpn(true).
        setSsl(true).
        setKeyCertOptions(new PemKeyCertOptions()
          .setKeyPath("io/vertx/example/core/http2/customframes/server-key.pem")
          .setCertPath("io/vertx/example/core/http2/customframes/server-cert.pem")
        ));

    return server.requestHandler(req -> {
      HttpServerResponse resp = req.response();

      req.customFrameHandler(frame -> {
        System.out.println("Received client frame " + frame.payload().toString("UTF-8"));

        // Write the sam
        resp.writeCustomFrame(10, 0, Buffer.buffer("pong"));
      });
    }).listen(8443);
  }
}
