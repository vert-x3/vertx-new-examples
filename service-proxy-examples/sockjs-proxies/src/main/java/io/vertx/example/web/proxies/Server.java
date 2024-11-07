package io.vertx.example.web.proxies;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.SockJSBridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.launcher.application.VertxApplication;
import io.vertx.serviceproxy.ServiceBinder;

/*
 * @author <a href="mailto:plopes@redhat.com">Paulo Lopes</a>
 */
public class Server extends VerticleBase {

  public static void main(String[] args) {
    VertxApplication.main(new String[]{Server.class.getName()});
  }

  @Override
  public Future<?> start() {

    // Create the client object
    MyService service = new MyServiceImpl();

    // Register the handler
    new ServiceBinder(vertx)
      .setAddress("proxy.example")
      .register(MyService.class, service);

    Router router = Router.router(vertx);

    SockJSBridgeOptions options = new SockJSBridgeOptions()
      .addInboundPermitted(new PermittedOptions().setAddress("proxy.example"));

    router.route("/eventbus*").subRouter(SockJSHandler.create(vertx).bridge(options));

    // Serve the static resources
    router.route().handler(StaticHandler.create());

    return vertx
      .createHttpServer()
      .requestHandler(router)
      .listen(8080);
  }
}
