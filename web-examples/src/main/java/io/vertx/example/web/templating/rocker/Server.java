package io.vertx.example.web.templating.rocker;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.TemplateHandler;
import io.vertx.ext.web.templ.rocker.RockerTemplateEngine;
import io.vertx.launcher.application.VertxApplication;

/**
 * @author <a href="mailto:danielrauf@gmail.com>Daniel Rauf</a>
 */
public class Server extends VerticleBase {

  public static void main(String[] args) {
    VertxApplication.main(new String[]{Server.class.getName()});
  }

  @Override
  public Future<?> start() throws Exception {

    final Router router = Router.router(vertx);

    // Populate context with data
    router.route().handler(ctx -> {
      ctx.put("title", "Vert.x Web Example Using Rocker");
      ctx.put("name", "Rocker");
      ctx.put("path", ctx.request().path());
      ctx.next();
    });

    // Render a custom template.
    // Note: you need a compile-time generator for Rocker to work properly
    // See the pom.xml for an example
    router.route().handler(TemplateHandler.create(RockerTemplateEngine.create()));

    return vertx
      .createHttpServer()
      .requestHandler(router)
      .listen(8080);
  }
}
