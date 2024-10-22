package io.vertx.example.web.proxy;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;

public class Backend extends VerticleBase {

  @Override
  public Future<?> start() throws Exception {

    HttpServer backendServer = vertx.createHttpServer();

    Router backendRouter = Router.router(vertx);

    backendRouter
      .get("/foo")
      .respond(ctx -> Future.succeededFuture("<html><body><h1>I'm the target '/foo' resource!</h1></body></html>"));

    backendRouter
      .get("/private")
      .respond(ctx -> Future.succeededFuture("<html><body><h1>I'm the target '/private' resource!</h1></body></html>"));

    return backendServer
      .requestHandler(backendRouter)
      .listen(7070);
  }
}
