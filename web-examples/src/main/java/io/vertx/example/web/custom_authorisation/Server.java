package io.vertx.example.web.custom_authorisation;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.JWTOptions;
import io.vertx.ext.auth.KeyStoreOptions;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authorization.PermissionBasedAuthorization;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.ext.auth.jwt.authorization.JWTAuthorization;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.JWTAuthHandler;
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

    // Create a JWT Auth Provider
    JWTAuth jwt = JWTAuth.create(vertx, new JWTAuthOptions()
      .setKeyStore(new KeyStoreOptions()
        .setType("jceks")
        .setPath("io/vertx/example/web/custom_authorisation/keystore.jceks")
        .setPassword("secret")));

    // this route is excluded from the auth handler (it represents your login endpoint)
    router.get("/api/newToken").handler(ctx -> {
      JsonArray authorities = new JsonArray();

      for (String authority : ctx.request().params().getAll("authority")) {
        authorities.add(authority);
      }

      ctx.response().putHeader("Content-Type", "text/plain");
      ctx.response().end(
        jwt.generateToken(new JsonObject().put("permissions", authorities), new JWTOptions().setExpiresInSeconds(60)));
    });

    router.route("/api/protected*").handler(JWTAuthHandler.create(jwt));

    JWTAuthorization authzProvider = JWTAuthorization.create("permissions");

    router.get("/api/protected").handler(ctx -> {
      // protected the API (any authority is allowed)
      ctx.response().putHeader("Content-Type", "text/plain");
      ctx.response().end("this secret is not defcon!");
    });

    PermissionBasedAuthorization defcon1 = PermissionBasedAuthorization.create("defcon1");
    router.get("/api/protected/defcon1").handler(ctx -> {
      User user = ctx.user().get();
      authzProvider.getAuthorizations(user).onComplete(ar -> {
        if (ar.succeeded()) {
          // protect the API (defcon1 authority is required)
          if (defcon1.match(user)) {
            ctx.response().putHeader("Content-Type", "text/plain");
            ctx.response().end("this secret is defcon1!");
          } else {
            ctx.response().setStatusCode(403).end();
          }
        } else {
          ctx.fail(ar.cause());
        }
      });
    });

    PermissionBasedAuthorization defcon2 = PermissionBasedAuthorization.create("defcon2");
    router.get("/api/protected/defcon2").handler(ctx -> {
      User user = ctx.user().get();
      authzProvider.getAuthorizations(user).onComplete(ar -> {
        if (ar.succeeded()) {
          // protect the API (defcon2 authority is required)
          if (defcon2.match(user)) {
            ctx.response().putHeader("Content-Type", "text/plain");
            ctx.response().end("this secret is defcon2!");
          } else {
            ctx.response().setStatusCode(403).end();
          }
        } else {
          ctx.fail(ar.cause());
        }
      });
    });

    PermissionBasedAuthorization defcon3 = PermissionBasedAuthorization.create("defcon3");
    router.get("/api/protected/defcon3").handler(ctx -> {
      User user = ctx.user().get();
      authzProvider.getAuthorizations(user).onComplete(ar -> {
        if (ar.succeeded()) {
          // protect the API (defcon3 authority is required)
          if (defcon3.match(user)) {
            ctx.response().putHeader("Content-Type", "text/plain");
            ctx.response().end("this secret is defcon3!");
          } else {
            ctx.response().setStatusCode(403).end();
          }
        } else {
          ctx.fail(ar.cause());
        }
      });
    });

    // Serve the non private static pages
    router.route().handler(StaticHandler.create("io/vertx/example/web/custom_authorisation/webroot"));

    return vertx
      .createHttpServer()
      .requestHandler(router)
      .listen(8080);
  }
}

