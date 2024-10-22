package io.vertx.example.core.http.websockets;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
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
    return vertx.createHttpServer()
      .webSocketHandler(ws -> ws.handler(ws::writeBinaryMessage))
      .requestHandler(req -> {
        if (req.uri().equals("/")) {
          req.response().sendFile("io/vertx/example/core/http/websockets/ws.html");
        };
      })
      .listen(8080);
  }
}
