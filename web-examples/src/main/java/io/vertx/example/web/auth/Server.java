package io.vertx.example.web.auth;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.ext.auth.properties.PropertyFileAuthentication;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.*;
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

    // We need sessions and request bodies
    router.route().handler(BodyHandler.create());
    router.route().handler(SessionHandler.create(LocalSessionStore.create(vertx)));

    // Simple auth service which uses a properties file for user/role info
    PropertyFileAuthentication authn = PropertyFileAuthentication.create(vertx, "vertx-users.properties");

    // Any requests to URI starting '/private/' require login
    router.route("/private/*").handler(RedirectAuthHandler.create(authn, "/loginpage.html"));

    // Serve the static private pages from directory 'private'
    router.route("/private/*").handler(StaticHandler.create("io/vertx/example/web/auth/private").setCachingEnabled(false));

    // Handles the actual login
    router.route("/loginhandler").handler(FormLoginHandler.create(authn));

    // Implement logout
    router.route("/logout").handler(context -> {
      context.userContext().logout();
      // Redirect back to the index page
      context.response().putHeader("location", "/").setStatusCode(302).end();
    });

    // Serve the non private static pages
    router.route().handler(StaticHandler.create("io/vertx/example/web/auth/webroot"));

    return vertx
      .createHttpServer()
      .requestHandler(router)
      .listen(8080);
  }
}

