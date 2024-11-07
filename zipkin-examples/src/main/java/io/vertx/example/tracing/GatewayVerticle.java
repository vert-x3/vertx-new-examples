package io.vertx.example.tracing;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.VerticleBase;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.client.WebClient;

import static io.vertx.core.http.HttpResponseExpectation.SC_OK;

public class GatewayVerticle extends VerticleBase implements Handler<HttpServerRequest> {

  private WebClient client;

  @Override
  public void handle(HttpServerRequest request) {
    switch (request.path()) {
      case "/hello":
        client.get(8081, "localhost", "/")
          .send()
          .expecting(SC_OK)
          .onSuccess(resp -> request.response().end(resp.body()))
          .onFailure(failure -> {
            failure.printStackTrace();
            request.response().setStatusCode(500).end();
          });
        break;
      case "/joke":
        client.get(8082, "localhost", "/")
          .send()
          .expecting(SC_OK)
          .onSuccess(resp -> request.response().end(resp.body()))
          .onFailure(failure -> {
            failure.printStackTrace();
            request.response().setStatusCode(500).end();
          });
        break;
      default:
        request.response().setStatusCode(404).end();
        break;
    }
  }

  @Override
  public Future<?> start() {
    client = WebClient.create(vertx);
    HttpServer server = vertx
      .createHttpServer()
      .requestHandler(this);
    return server.listen(8080);
  }
}
