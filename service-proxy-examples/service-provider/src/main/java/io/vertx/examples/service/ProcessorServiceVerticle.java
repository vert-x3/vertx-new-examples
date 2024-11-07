package io.vertx.examples.service;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.examples.service.impl.ProcessorServiceImpl;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.SockJSBridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.launcher.application.VertxApplication;
import io.vertx.serviceproxy.ServiceBinder;

/**
 * The verticle publishing the service.
 *
 * @author Lalit Rao
 */
public class ProcessorServiceVerticle extends VerticleBase {

  public static void main(String[] args) {
    VertxApplication.main(new String[]{ProcessorServiceVerticle.class.getName(), "-cluster"});
  }

  @Override
  public Future<?> start() {
    // Create the client object
    ProcessorService service = new ProcessorServiceImpl();
    // Register the handler
    new ServiceBinder(vertx)
      .setAddress("vertx.processor")
      .register(ProcessorService.class, service);

    Router router = Router.router(vertx);

    // Allow events for the designated addresses in/out of the event bus bridge
    SockJSBridgeOptions opts = new SockJSBridgeOptions()
      .addInboundPermitted(new PermittedOptions().setAddress("vertx.processor"))
      .addOutboundPermitted(new PermittedOptions().setAddress("vertx.processor"));

    // Create the event bus bridge and add it to the router.
    router.route("/eventbus*").subRouter(SockJSHandler.create(vertx).bridge(opts));
    router.route().handler(StaticHandler.create());

    //
    return vertx
      .createHttpServer()
      .requestHandler(router)
      .listen(8080);
  }

}
