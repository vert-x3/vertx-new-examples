package io.vertx.example.web.realtime;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.ext.bridge.BridgeEventType;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.SockJSBridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
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

    // Allow outbound traffic to the news-feed address

    SockJSBridgeOptions options = new SockJSBridgeOptions()
      .addOutboundPermitted(new PermittedOptions().setAddress("news-feed"));
    SockJSHandler sockJSHandler = SockJSHandler.create(vertx);
    Router subRouter = sockJSHandler.bridge(options, event -> {

      // You can also optionally provide a handler like this which will be passed any events that occur on the bridge
      // You can use this for monitoring or logging, or to change the raw messages in-flight.
      // It can also be used for fine-grained access control.

      if (event.type() == BridgeEventType.SOCKET_CREATED) {
        System.out.println("A socket was created");
      }

      // This signals that it's ok to process the event
      event.complete(true);

    });
    router.route("/eventbus/*").subRouter(subRouter);


    // Serve the static resources
    router.route().handler(StaticHandler.create("io/vertx/example/web/realtime/webroot"));

    // Publish a message to the address "news-feed" every second
    vertx.setPeriodic(1000, t -> vertx.eventBus().publish("news-feed", "news from the server!"));

    return vertx
      .createHttpServer()
      .requestHandler(router)
      .listen(8080);
  }
}
