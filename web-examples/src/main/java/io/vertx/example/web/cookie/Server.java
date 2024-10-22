package io.vertx.example.web.cookie;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.http.Cookie;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
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

    Router router = Router.router(vertx);

    // on every path increment the counter
    router.route().handler(ctx -> {
      Cookie someCookie = ctx.request().getCookie("visits");

      long visits = 0;
      if (someCookie != null) {
        String cookieValue = someCookie.getValue();
        try {
          visits = Long.parseLong(cookieValue);
        } catch (NumberFormatException e) {
          visits = 0;
        }
      }

      // increment the tracking
      visits++;

      // Add a cookie - this will get written back in the response automatically
      ctx.response().addCookie(Cookie.cookie("visits", "" + visits));

      ctx.next();
    });

    // Serve the static resources
    router.route().handler(StaticHandler.create("io/vertx/example/web/cookie/webroot"));

    return vertx
      .createHttpServer()
      .requestHandler(router)
      .listen(8080);
  }
}
