package io.vertx.example.camel.rmi;

import io.vertx.camel.CamelBridge;
import io.vertx.camel.CamelBridgeOptions;
import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import org.apache.camel.CamelContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static io.vertx.camel.OutboundMapping.fromVertx;

/**
 * @author <a href="http://escoffier.me">Clement Escoffier</a>
 */
public class RMIExample extends VerticleBase {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(RMIExample.class.getName());
  }


  @Override
  public Future<?> start() {
    ApplicationContext app = new ClassPathXmlApplicationContext("META-INF/spring/camelContext.xml");
    CamelContext camel = app.getBean("camel", CamelContext.class);

    CamelBridge.create(vertx, new CamelBridgeOptions(camel)
      .addOutboundMapping(fromVertx("invocation").toCamel("rmiService")))
      .start();

    return vertx.createHttpServer()
      .requestHandler(this::invoke)
      .listen(8080);
  }

  private void invoke(HttpServerRequest request) {
    String param = request.getParam("name");
    if (param == null) {
      param = "vert.x";
    }
    vertx.eventBus().<String>request("invocation", param)
      .onComplete(reply -> {
      if (reply.failed()) {
        request.response().setStatusCode(400).end(reply.cause().getMessage());
      } else {
        request.response().setStatusCode(400).end(reply.result().body());
      }
    });
  }
}
