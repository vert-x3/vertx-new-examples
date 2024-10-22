package io.vertx.example.web.react;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.SockJSBridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
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

    // Allow events for the designated addresses in/out of the event bus bridge
    SockJSBridgeOptions opts = new SockJSBridgeOptions()
      .addInboundPermitted(new PermittedOptions().setAddress("chat.message"))
      .addOutboundPermitted(new PermittedOptions().setAddress("chat.message"));

    // Create the event bus bridge and add it to the router.
    SockJSHandler ebHandler = SockJSHandler.create(vertx);
    router.route("/eventbus*").subRouter(ebHandler.bridge(opts));

    // Create a router endpoint for the static content.
    router.route().handler(StaticHandler.create("io/vertx/example/web/react/webroot"));

    // Start the web server and tell it to use the router to handle requests.
    return vertx
      .createHttpServer()
      .requestHandler(router)
      .listen(8080);
  }
}
