package io.vertx.example.micrometer.verticles;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.ext.web.Router;

/**
 * @author Joel Takvorian, jtakvori@redhat.com
 */
public class SimpleWebServer extends VerticleBase {
  @Override
  public Future<?> start() throws Exception {
    Router router = Router.router(vertx);
    router.get("/").handler(ctx -> {
      Greetings.get(vertx).onComplete(greetingResult -> ctx.response().end(greetingResult.result()));
    });
    return vertx
      .createHttpServer()
      .requestHandler(router)
      .listen(8080);
  }
}
