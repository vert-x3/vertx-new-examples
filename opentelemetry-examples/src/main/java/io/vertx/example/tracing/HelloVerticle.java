package io.vertx.example.tracing;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.ext.web.client.WebClient;

import static io.vertx.core.http.HttpResponseExpectation.SC_OK;
import static io.vertx.core.http.HttpResponseExpectation.contentType;

public class HelloVerticle extends VerticleBase {

  private WebClient client;

  @Override
  public Future<?> start() {
    client = WebClient.create(vertx);
    return vertx.createHttpServer().requestHandler(req -> {
      client
        .get(8082, "localhost", "/")
        .send()
        .expecting(SC_OK.and(contentType("text/plain")))
        .onSuccess(resp -> {
          req.response().end("Hello, here is a joke for you \"" + resp.bodyAsString() + "\"");
        })
        .onFailure(failure -> {
          failure.printStackTrace();
          req.response().end("Hello, sorry no joke for you today");
        });
    }).listen(8081);
  }
}
