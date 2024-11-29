package io.vertx.example.tracing;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;

import static io.vertx.core.http.HttpResponseExpectation.SC_OK;

public class GatewayVerticle extends VerticleBase {

  private WebClient client;

  @Override
  public Future<?> start() {
    client = WebClient.create(vertx);
    Router router = Router.router(vertx);
    router.get("/hello").respond(rc -> {
      return client.get(8081, "localhost", "/")
        .send()
        .expecting(SC_OK)
        .map(HttpResponse::bodyAsString)
        .onFailure(Throwable::printStackTrace);
    });
    router.get("/joke").respond(rc -> {
      return client.get(8082, "localhost", "/")
        .send()
        .expecting(SC_OK)
        .map(HttpResponse::bodyAsString)
        .onFailure(Throwable::printStackTrace);
    });
    HttpServer server = vertx.createHttpServer().requestHandler(router);
    return server.listen(8080);
  }
}
