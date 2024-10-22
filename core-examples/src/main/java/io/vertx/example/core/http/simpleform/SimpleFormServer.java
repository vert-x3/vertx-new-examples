package io.vertx.example.core.http.simpleform;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.launcher.application.VertxApplication;

/*
 * NOTE! It's recommended to use Vert.x-Web for examples like this
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class SimpleFormServer extends VerticleBase {

  public static void main(String[] args) {
    VertxApplication.main(new String[]{SimpleFormServer.class.getName()});
  }

  @Override
  public Future<?> start() throws Exception {
    return vertx
      .createHttpServer()
      .requestHandler(req -> {
        if (req.uri().equals("/")) {
          // Serve the index page
          req.response().sendFile("io/vertx/example/core/http/simpleform/index.html");
        } else if (req.uri().startsWith("/form")) {
          req.response().setChunked(true);
          req.setExpectMultipart(true);
          req.endHandler((v) -> {
            for (String attr : req.formAttributes().names()) {
              req.response().write("Got attr " + attr + " : " + req.formAttributes().get(attr) + "\n");
            }
            req.response().end();
          });
        } else {
          req.response().setStatusCode(404).end();
        }
      }).listen(8080);
  }
}
