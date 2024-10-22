package io.vertx.example.web.oauth2;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.TokenCredentials;
import io.vertx.ext.auth.oauth2.OAuth2Auth;
import io.vertx.ext.auth.oauth2.providers.GithubAuth;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.ext.web.handler.OAuth2AuthHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;
import io.vertx.ext.web.templ.handlebars.HandlebarsTemplateEngine;
import io.vertx.launcher.application.VertxApplication;

/*
 * @author <a href="mailto:plopes@redhat.com">Paulo Lopes</a>
 */
public class Server extends VerticleBase {

  public static void main(String[] args) {
    VertxApplication.main(new String[]{Server.class.getName()});
  }

  // you should never store these in code,
  // these are your GitHub application credentials
  private static final String CLIENT_ID = "57cdaa1952a3f4ee3df8";
  private static final String CLIENT_SECRET = "3155eafd33fc947e0fe9f44127055ce1fe876704";

  @Override
  public Future<?> start() throws Exception {
    // In order to use a template we first need to create an engine
    final HandlebarsTemplateEngine engine = HandlebarsTemplateEngine.create(vertx);

    // To simplify the development of the web components we use a Router to route all HTTP requests
    // to organize our code in a reusable way.
    final Router router = Router.router(vertx);
    // We need cookies and sessions
    router.route().handler(SessionHandler.create(LocalSessionStore.create(vertx)));
    // Simple auth service which uses a GitHub to authenticate the user
    OAuth2Auth authProvider = GithubAuth.create(vertx, CLIENT_ID, CLIENT_SECRET);
    // we now protect the resource under the path "/protected"
    router.route("/protected").handler(OAuth2AuthHandler.create(vertx, authProvider, "http://localhost:8080/callback")
      // we now configure the oauth2 handler, it will setup the callback handler
      // as expected by your oauth2 provider.
      .setupCallback(router.route("/callback"))
      // for this resource we require that users have the authority to retrieve the user emails
      .withScope("user:email")
    );
    // Entry point to the application, this will render a custom template.
    router.get("/").handler(ctx -> {
      // we pass the client id to the template
      JsonObject data = new JsonObject()
        .put("client_id", CLIENT_ID);
      // and now delegate to the engine to render it.
      engine
        .render(data, "io/vertx/example/web/oauth2/views/index.hbs")
        .onComplete(res -> {
        if (res.succeeded()) {
          ctx.response()
            .putHeader("Content-Type", "text/html")
            .end(res.result());
        } else {
          ctx.fail(res.cause());
        }
      });
    });
    // The protected resource
    router.get("/protected").handler(ctx -> {
      User user = ctx.user().get();
      // retrieve the user profile, this is a common feature but not from the official OAuth2 spec
      authProvider
        .userInfo(user)
        .onComplete(res -> {
        if (res.failed()) {
          // request didn't succeed because the token was revoked so we
          // invalidate the token stored in the session and render the
          // index page so that the user can start the OAuth flow again
          ctx.session().destroy();
          ctx.fail(res.cause());
        } else {
          // the request succeeded, so we use the API to fetch the user's emails
          final JsonObject userInfo = res.result();

          // fetch the user emails from the github API

          // the web client will retrieve any resource and ensure the right
          // secure headers are passed.
          WebClient.create(vertx)
            .getAbs("https://api.github.com/user/emails")
            .authentication(new TokenCredentials(user.<String>get("access_token")))
            .as(BodyCodec.jsonArray())
            .send()
            .onComplete(res2 -> {
              if (res2.failed()) {
                // request didn't succeed because the token was revoked so we
                // invalidate the token stored in the session and render the
                // index page so that the user can start the OAuth flow again
                ctx.session().destroy();
                ctx.fail(res2.cause());
              } else {
                userInfo.put("private_emails", res2.result().body());
                // we pass the client info to the template
                JsonObject data = new JsonObject()
                  .put("userInfo", userInfo);
                // and now delegate to the engine to render it.
                engine.render(data, "io/vertx/example/web/oauth2/views/advanced.hbs").onComplete(res3 -> {
                  if (res3.succeeded()) {
                    ctx.response()
                      .putHeader("Content-Type", "text/html")
                      .end(res3.result());
                  } else {
                    ctx.fail(res3.cause());
                  }
                });
              }
            });
        }
      });
    });

    return vertx
      .createHttpServer()
      .requestHandler(router)
      .listen(8080);
  }
}
