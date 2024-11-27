package io.vertx.example.core.http.graceful_shutdown;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static io.vertx.example.core.http.graceful_shutdown.Util.log;

public class Server extends VerticleBase {

  private HttpServer httpServer;

  public static void main(String[] args) throws Exception {
    Vertx vertx = Vertx.vertx();
    CountDownLatch requestLatch = new CountDownLatch(1);
    vertx.deployVerticle(new Server(requestLatch)).await();
    log("Server started");
    requestLatch.await();
    vertx.close().await();
    log("Vert.x is closed");
  }

  private final int port;
  private final CountDownLatch requestLatch;

  public Server(int port, CountDownLatch requestLatch) {
    this.port = port;
    this.requestLatch = requestLatch;
  }

  public Server(CountDownLatch requestLatch) {
    this(8080, requestLatch);
  }

  @Override
  public Future<?> start() throws Exception {
    httpServer = vertx.createHttpServer();
    return httpServer
      .requestHandler(req -> {
        log("Request handled");
        requestLatch.countDown();
        vertx.setTimer(5000, l -> {
          req.response()
            .putHeader("content-type", "text/html")
            .end("<html><body><h1>Hello from Vert.x!</h1></body></html>");
          log("Response sent");
        });
      })
      .listen(port);
  }

  @Override
  public Future<?> stop() throws Exception {
    log("Shutting down HTTP server");
    return httpServer.shutdown(5, TimeUnit.MINUTES);
  }
}
