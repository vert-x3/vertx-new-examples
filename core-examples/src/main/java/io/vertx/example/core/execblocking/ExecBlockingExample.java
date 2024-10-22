package io.vertx.example.core.execblocking;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.launcher.application.VertxApplication;

/*
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class ExecBlockingExample extends VerticleBase {

  public static void main(String[] args) {
    VertxApplication.main(new String[]{ExecBlockingExample.class.getName()});
  }

  @Override
  public Future<?> start() throws Exception {

    return vertx.createHttpServer().requestHandler(request -> {

      // Let's say we have to call a blocking API (e.g. JDBC) to execute a query for each
      // request. We can't do this directly or it will block the event loop
      // But you can do this using executeBlocking:

      vertx.executeBlocking(() -> {

        // Do the blocking operation in here

        // Imagine this was a call to a blocking API to get the result
        try {
          Thread.sleep(500);
        } catch (Exception ignore) {
        }

        return "armadillos!";
      })
        .onSuccess(res -> request
          .response()
          .putHeader("content-type", "text/plain")
          .end(res))
        .onFailure(err -> request
          .response()
          .setStatusCode(500)
          .end());

    }).listen(8080);
  }
}
