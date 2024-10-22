package io.vertx.example.web.authorisation;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.JWTOptions;
import io.vertx.ext.auth.KeyStoreOptions;
import io.vertx.ext.auth.authorization.PermissionBasedAuthorization;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.ext.auth.jwt.authorization.JWTAuthorization;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.AuthorizationHandler;
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
        .setPath("io/vertx/example/web/authorisation/keystore.jceks")
        .setPassword("secret")));

    // this route is excluded from the auth handler (it represents your login endpoint)
    router.get("/api/newToken").handler(ctx -> {

      JsonArray authorities = new JsonArray();

      for (String authority : ctx.request().params().getAll("authority")) {
        authorities.add(authority);
      }

      ctx.response().putHeader("Content-Type", "text/plain");
      ctx.response().end(jwt.generateToken(
        new JsonObject()
          .put("permissions", authorities),
        new JWTOptions().setExpiresInSeconds(60)));
    });

    JWTAuthHandler authnHandler = JWTAuthHandler.create(jwt);
    JWTAuthorization authzProvider = JWTAuthorization.create("permissions");

    // protect the API (any authority is allowed)
    router.route("/api/*").handler(authnHandler);

    router.get("/api/protected").handler(ctx -> {
      ctx.response().putHeader("Content-Type", "text/plain");
      ctx.response().end("this secret is not defcon!");
    });

    // protect the API (defcon1 authority is required)
    AuthorizationHandler defcon1Handler = AuthorizationHandler.create(PermissionBasedAuthorization.create("defcon1")).addAuthorizationProvider(authzProvider);
    router.route("/api/protected/defcon1").handler(defcon1Handler);

    router.get("/api/protected/defcon1").handler(ctx -> {
      ctx.response().putHeader("Content-Type", "text/plain");
      ctx.response().end("this secret is defcon1!");
    });

    // protect the API (defcon2 authority is required)
    AuthorizationHandler defcon2Handler = AuthorizationHandler.create(PermissionBasedAuthorization.create("defcon2")).addAuthorizationProvider(authzProvider);
    router.route("/api/protected/defcon2").handler(defcon2Handler);

    router.get("/api/protected/defcon2").handler(ctx -> {
      ctx.response().putHeader("Content-Type", "text/plain");
      ctx.response().end("this secret is defcon2!");
    });

    // protect the API (defcon3 authority is required)
    AuthorizationHandler defcon3Handler = AuthorizationHandler.create(PermissionBasedAuthorization.create("defcon3")).addAuthorizationProvider(authzProvider);
    router.route("/api/protected/defcon3").handler(defcon3Handler);

    router.get("/api/protected/defcon3").handler(ctx -> {
      ctx.response().putHeader("Content-Type", "text/plain");
      ctx.response().end("this secret is defcon3!");
    });

    // Serve the non-private static pages
    router.route().handler(StaticHandler.create("io/vertx/example/web/authorisation/webroot"));

    return vertx
      .createHttpServer()
      .requestHandler(router)
      .listen(8080);
  }
}

