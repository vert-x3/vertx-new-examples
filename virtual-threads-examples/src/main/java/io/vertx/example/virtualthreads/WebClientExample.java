package io.vertx.example.virtualthreads;

import io.vertx.core.*;
import io.vertx.core.http.*;
import io.vertx.ext.web.client.WebClient;

public class WebClientExample extends VerticleBase {

  public static void main(String[] args) {
    var vertx = Vertx.vertx();
    vertx.deployVerticle(WebClientExample.class, new DeploymentOptions()
        .setThreadingModel(ThreadingModel.VIRTUAL_THREAD))
      .await();
  }

  @Override
  public Future<?> start() throws Exception {
    var server = vertx.createHttpServer();
    server.requestHandler(request -> {
      request.response().end("Hello World");
    });
    server.listen(8080, "localhost").await();

    // Make a simple HTTP request
    var client = WebClient.create(vertx);
    var resp = client
      .get(8080, "localhost", "/")
      .send()
      .expecting(HttpResponseExpectation.SC_OK)
      .await();
    var status = resp.statusCode();
    var body = resp.body();
    System.out.println("Got response status=" + status + " body='" + body + "'");
    return super.start();
  }
}
