package io.vertx.example.core.http.simpleformupload;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.launcher.application.VertxApplication;

/*
 * NOTE! It's recommended to use Vert.x-Web for handling file uploads otherwise it's easy to get caught
 * by malicious requests which might craft an upload to save it in a place where you don't want it to be saved.
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class SimpleFormUploadServer extends VerticleBase {

  public static void main(String[] args) {
    VertxApplication.main(new String[]{SimpleFormUploadServer.class.getName()});
  }

  @Override
  public Future<?> start() throws Exception {
    return vertx
      .createHttpServer()
      .requestHandler(req -> {
        if (req.uri().equals("/")) {
          // Serve the index page
          req.response().sendFile("io/vertx/example/core/http/simpleformupload/index.html");
        } else if (req.uri().startsWith("/form")) {
          req.setExpectMultipart(true);
          req.uploadHandler(upload -> {
            // FIXME - Potential security exploit! In a real system you must check this filename
            // to make sure you're not saving to a place where you don't want!
            // Or better still, just use Vert.x-Web which controls the upload area.
            upload.streamToFileSystem(upload.filename())
              .onSuccess(v -> req.response().end("Successfully uploaded to " + upload.filename()))
              .onFailure(err -> req.response().end("Upload failed"));
          });
        } else {
          req.response()
            .setStatusCode(404)
            .end();
        }
      }).listen(8080);
  }
}
