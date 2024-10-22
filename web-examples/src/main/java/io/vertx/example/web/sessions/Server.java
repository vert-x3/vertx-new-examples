package io.vertx.example.web.sessions;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.Session;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;
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

    Router router = Router.router(vertx);

    router.route().handler(SessionHandler.create(LocalSessionStore.create(vertx)));

    router.route().handler(routingContext -> {

      Session session = routingContext.session();

      Integer cnt = session.get("hitcount");
      cnt = (cnt == null ? 0 : cnt) + 1;

      session.put("hitcount", cnt);

      routingContext.response().putHeader("content-type", "text/html")
                               .end("<html><body><h1>Hitcount: " + cnt + "</h1></body></html>");
    });

    return vertx
      .createHttpServer()
      .requestHandler(router)
      .listen(8080);
  }
}
