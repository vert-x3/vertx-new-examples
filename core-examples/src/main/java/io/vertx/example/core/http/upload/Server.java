package io.vertx.example.core.http.upload;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.OpenOptions;
import io.vertx.core.streams.Pipe;
import io.vertx.launcher.application.VertxApplication;

import java.util.UUID;

/*
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class Server extends VerticleBase {

  public static void main(String[] args) {
    VertxApplication.main(new String[]{Server.class.getName()});
  }

  @Override
  public Future<?> start() throws Exception {
    return vertx
      .createHttpServer()
      .requestHandler(req -> {
        Pipe<Buffer> pipe = req
          .pipe()
          .endOnComplete(true);
        String filename = UUID.randomUUID() + ".uploaded";
        vertx.fileSystem()
          .open(filename, new OpenOptions())
          .transform(ar -> {
            if (ar.succeeded()) {
              return pipe.to(ar.result()).onComplete(ar2 -> {
                System.out.println("Uploaded to " + filename);
              });
            } else {
              return req.response()
                .setStatusCode(500)
                .end("Could not upload file");
            }
          });
      })
      .listen(8080);
  }
}
