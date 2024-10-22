package io.vertx.example.web.blockinghandler;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.ext.web.Router;
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

    router.route().blockingHandler(routingContext -> {
      // Blocking handlers are allowed to block the calling thread
      // So let's simulate a blocking action or long running operation
      try {
        Thread.sleep(5000);
      } catch (Exception ignore) {
      }

      // Now call the next handler
      routingContext.next();
    }, false);

    router.route().handler(routingContext -> {
      routingContext.response().putHeader("content-type", "text/html").end("Hello World!");
    });

    return vertx
      .createHttpServer()
      .requestHandler(router)
      .listen(8080);
  }
}
