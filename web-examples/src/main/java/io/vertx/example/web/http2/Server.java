package io.vertx.example.web.http2;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.net.PemKeyCertOptions;
import io.vertx.ext.web.Router;
import io.vertx.launcher.application.VertxApplication;

/*
 * @author <a href="mailto:pmlopes@gmail.com">Paulo Lopes</a>
 */
public class Server extends VerticleBase {

  public static void main(String[] args) {
    VertxApplication.main(new String[]{Server.class.getName()});
  }

  @Override
  public Future<?> start() throws Exception {

    final Image image = new Image(vertx, "coin.png");

    Router router = Router.router(vertx);

    router.get("/").handler(ctx -> {
      ctx.response()
              .putHeader("Content-Type", "text/html")
              .end(image.generateHTML(16));
    });

    router.get("/img/:x/:y").handler(ctx -> {
      ctx.response()
              .putHeader("Content-Type", "image/png")
              .end(image.getPixel(Integer.parseInt(ctx.pathParam("x")), Integer.parseInt(ctx.pathParam("y"))));
    });

    return vertx.createHttpServer(
        new HttpServerOptions()
          .setSsl(true)
          .setUseAlpn(true)
          .setKeyCertOptions(new PemKeyCertOptions().setKeyPath("tls/server-key.pem").setCertPath("tls/server-cert.pem")))
      .requestHandler(router)
      .listen(8443);
  }
}
