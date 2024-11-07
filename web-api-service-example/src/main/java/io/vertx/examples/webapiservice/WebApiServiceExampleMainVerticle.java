package io.vertx.examples.webapiservice;

import io.vertx.core.*;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.examples.webapiservice.persistence.TransactionPersistence;
import io.vertx.examples.webapiservice.services.TransactionsManagerService;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.openapi.RouterBuilder;
import io.vertx.serviceproxy.ServiceBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebApiServiceExampleMainVerticle extends VerticleBase {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new WebApiServiceExampleMainVerticle());
  }

  HttpServer server;
  ServiceBinder serviceBinder;

  MessageConsumer<JsonObject> consumer;
  Logger LOG = LoggerFactory.getLogger(this.getClass());

  /**
   * Start transaction service
   */
  private void startTransactionService() {
    serviceBinder = new ServiceBinder(vertx);

    TransactionPersistence persistence = TransactionPersistence.create();

    // Create an instance of TransactionManagerService and mount to event bus
    TransactionsManagerService transactionsManagerService = TransactionsManagerService.create(persistence);
    consumer = serviceBinder
        .setAddress("transactions_manager.myapp")
        .register(TransactionsManagerService.class, transactionsManagerService);
  }

  /**
   * This method constructs the router factory, mounts services and handlers and starts the http server with built router
   * @return
   */
  private Future<Void> startHttpServer() {
    return RouterBuilder.create(this.vertx, "openapi.json")
      .onFailure(Throwable::printStackTrace) // In case the contract loading failed print the stacktrace
      .compose(routerBuilder -> {
        // Mount services on event bus based on extensions
        routerBuilder.mountServicesFromExtensions();

        // Generate the router
        Router router = routerBuilder.createRouter();
        router.errorHandler(400, ctx -> {
          LOG.debug("Bad Request", ctx.failure());
        });
        server = vertx.createHttpServer(new HttpServerOptions().setPort(8080).setHost("localhost")).requestHandler(router);
        return server.listen().mapEmpty();
      });
  }

  @Override
  public Future<?> start() {
    startTransactionService();
    return startHttpServer();
  }

  /**
   * This method closes the http server and unregister all services loaded to Event Bus
   */
  @Override
  public Future<?> stop(){
    Future<Void> fut = server.close();
    consumer.unregister();
    return fut;
  }
}
