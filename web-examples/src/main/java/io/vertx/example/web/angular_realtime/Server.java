package io.vertx.example.web.angular_realtime;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.authentication.UsernamePasswordCredentials;
import io.vertx.ext.auth.properties.PropertyFileAuthentication;
import io.vertx.ext.auth.properties.PropertyFileAuthorization;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.handler.SimpleAuthenticationHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.SockJSBridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;
import io.vertx.launcher.application.VertxApplication;

import java.util.LinkedList;
import java.util.List;

/*
 * @author <a href="mailto:pmlopes@gmail.com">Paulo Lopes</a>
 */
public class Server extends VerticleBase {

  public static void main(String[] args) {
    VertxApplication.main(new String[]{Server.class.getName()});
  }

  private MongoClient mongo;

  @Override
  public Future<?> start() throws Exception {

    // Create a mongo client using all defaults (connect to localhost and default port) using the database name "demo".
    mongo = MongoClient.createShared(vertx, new JsonObject().put("db_name", "demo"));

    // the load function just populates some data on the storage
    loadData(mongo);

    // the app works 100% realtime
    vertx.eventBus().consumer("vtoons.listAlbums", this::listAlbums);
    vertx.eventBus().consumer("vtoons.placeOrder", this::placeOrder);

    Router router = Router.router(vertx);

    // We need cookies and sessions
    router.route().handler(BodyHandler.create());
    router.route().handler(SessionHandler.create(LocalSessionStore.create(vertx)));

    // Simple auth service which uses a properties file for user/role info
    PropertyFileAuthentication authProvider = PropertyFileAuthentication.create(vertx, "vertx-users.properties");

    // Use the auth handler to perform the authentication for us
    router.post("/login").handler(SimpleAuthenticationHandler.create().authenticate(ctx -> {
      JsonObject credentials = ctx.body().asJsonObject();
      if (credentials == null) {
        return Future.failedFuture("No credentials");
      }
      return authProvider.authenticate(new UsernamePasswordCredentials(credentials));
    })).handler(ctx -> {
      ctx.json(new JsonObject());
    });

    router.route("/eventbus/*").handler(ctx -> {
      // we need to be logged in
      if (ctx.user() == null) {
        ctx.fail(403);
      } else {
        ctx.next();
      }
    });

    // Allow outbound traffic to the vtoons addresses
    SockJSBridgeOptions options = new SockJSBridgeOptions()
      // this is the public store allowed inbound
      .addInboundPermitted(new PermittedOptions()
        .setAddress("vtoons.listAlbums"))
      // this is the login inbound message
      .addInboundPermitted(new PermittedOptions()
        .setAddress("vtoons.login"))
      // this is the authenticated place orders inbound
      .addInboundPermitted(new PermittedOptions()
        .setAddress("vtoons.placeOrder")
        .setRequiredAuthority("place_order"))

      // all outbound messages are permitted
      .addOutboundPermitted(new PermittedOptions());

    SockJSHandler sockJSHandler = SockJSHandler.create(vertx);
    PropertyFileAuthorization authorizationProvider = PropertyFileAuthorization.create(vertx, "vertx-users.properties");
    router.route("/eventbus*").subRouter(sockJSHandler.bridge(authorizationProvider, options, null));


    // Serve the static resources
    router.route().handler(StaticHandler.create("io/vertx/example/web/angular_realtime/webroot"));

    return vertx
      .createHttpServer()
      .requestHandler(router)
      .listen(8080);
  }

  private void listAlbums(Message<JsonObject> msg) {
    // issue a find command to mongo to fetch all documents from the "albums" collection.
    mongo.find("albums", new JsonObject()).onComplete(lookup -> {
      // error handling
      if (lookup.failed()) {
        msg.fail(500, lookup.cause().getMessage());
        return;
      }

      // now convert the list to a JsonArray because it will be easier to encode the final object as the response.
      final JsonArray json = new JsonArray();

      for (JsonObject o : lookup.result()) {
        json.add(o);
      }

      msg.reply(json);
    });

  }

  private void placeOrder(Message<JsonObject> msg) {
    mongo.save("orders", msg.body()).onComplete(save -> {
      // error handling
      if (save.failed()) {
        msg.fail(500, save.cause().getMessage());
        return;
      }

      msg.reply(new JsonObject());
    });
  }

  private void loadData(MongoClient db) {
    db.dropCollection("albums").onComplete(drop -> {
      if (drop.failed()) {
        throw new RuntimeException(drop.cause());
      }

      List<JsonObject> albums = new LinkedList<>();

      albums.add(new JsonObject()
        .put("artist", "The Wurzels")
        .put("genre", "Scrumpy and Western")
        .put("title", "I Am A Cider Drinker")
        .put("price", 0.99));

      albums.add(new JsonObject()
        .put("artist", "Vanilla Ice")
        .put("genre", "Hip Hop")
        .put("title", "Ice Ice Baby")
        .put("price", 0.01));

      albums.add(new JsonObject()
        .put("artist", "Ena Baga")
        .put("genre", "Easy Listening")
        .put("title", "The Happy Hammond")
        .put("price", 0.50));


      albums.add(new JsonObject()
        .put("artist", "The Tweets")
        .put("genre", "Bird related songs")
        .put("title", "The Birdy Song")
        .put("price", 1.20));

      for (JsonObject album : albums) {
        db.insert("albums", album)
          .onComplete(res -> {
          System.out.println("inserted " + album.encode());
        });
      }
    });
  }
}
