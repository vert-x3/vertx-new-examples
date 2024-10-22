package io.vertx.example.web.templating.pebble;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.templ.pebble.PebbleTemplateEngine;
import io.vertx.launcher.application.VertxApplication;

/**
 * This is an example application to showcase the usage of Vert.x Web.
 *
 * In this application you will see the usage of:
 *
 *  * Pebble templates
 *  * Vert.x Web
 *
 * @author <a href="mailto:pmlopes@gmail.com>Paulo Lopes</a>
 */
public class Server extends VerticleBase {

  public static void main(String[] args) {
    VertxApplication.main(new String[]{Server.class.getName()});
  }

  @Override
  public Future<?> start() throws Exception {

    // To simplify the development of the web components we use a Router to route all HTTP requests
    // to organize our code in a reusable way.
    final Router router = Router.router(vertx);

    // In order to use a template we first need to create an engine
    final PebbleTemplateEngine engine = PebbleTemplateEngine.create(vertx);

    // Entry point to the application, this will render a custom template.
    router.get().handler(ctx -> {
      // we define a hardcoded title for our application
      JsonObject data = new JsonObject()
        .put("name", "Vert.x Web")
        .put("path", ctx.request().path());

      // and now delegate to the engine to render it.
      engine.render(data, "io/vertx/example/web/templating/pebble/templates/index.peb").onComplete(res -> {
        if (res.succeeded()) {
          ctx.response().end(res.result());
        } else {
          ctx.fail(res.cause());
        }
      });
    });

    // start an HTTP web server on port 8080
    return vertx
      .createHttpServer()
      .requestHandler(router)
      .listen(8080);
  }
}
