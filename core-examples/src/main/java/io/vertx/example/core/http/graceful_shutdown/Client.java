package io.vertx.example.core.http.graceful_shutdown;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpMethod;

import java.util.concurrent.CountDownLatch;

import static io.vertx.example.core.http.graceful_shutdown.Util.log;

public class Client extends AbstractVerticle {

  public static void main(String[] args) throws Exception {
    Vertx vertx = Vertx.vertx();
    CountDownLatch responseLatch = new CountDownLatch(1);
    vertx.deployVerticle(new Client(responseLatch)).await();
    log("Client started");
    responseLatch.await();
    vertx.close().await();
    log("Vert.x is closed");
  }

  private final CountDownLatch responseLatch;
  private HttpClient client;

  public Client(CountDownLatch responseLatch) {
    this.responseLatch = responseLatch;
  }

  @Override
  public void start() {
    client = vertx.createHttpClient();
    client
      .request(HttpMethod.POST, 8080, "localhost", "/")
      .compose(req -> {
        log("Sending request");
        return req.send().compose(resp -> {
          log("Got response " + resp.statusCode());
          return resp.body();
        });
      })
      .onComplete(ar -> {
        if (ar.succeeded()) {
          Buffer body = ar.result();
          log("Got data " + body.toString("ISO-8859-1"));
        }
        responseLatch.countDown();
      });
  }
}
