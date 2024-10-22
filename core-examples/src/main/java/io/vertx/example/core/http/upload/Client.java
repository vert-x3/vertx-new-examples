package io.vertx.example.core.http.upload;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.file.FileSystem;
import io.vertx.core.file.OpenOptions;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpMethod;
import io.vertx.launcher.application.VertxApplication;

/*
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class Client extends VerticleBase {

  public static void main(String[] args) {
    VertxApplication.main(new String[]{Client.class.getName()});
  }

  private HttpClient client;

  @Override
  public Future<?> start() throws Exception {
    client = vertx.createHttpClient(new HttpClientOptions());
    return client
      .request(HttpMethod.PUT, 8080, "localhost", "/someurl")
      .compose(req -> {
        String filename = "io/vertx/example/core/http/upload/upload.txt";
        FileSystem fs = vertx.fileSystem();
        return fs
          .props(filename)
          .compose(props -> {
            long size = props.size();
            req.headers().set("content-length", "" + size);
            return fs.open(filename, new OpenOptions());
          }).compose(file -> req.send(file)
            .map(HttpClientResponse::statusCode));
      }).onSuccess(statusCode -> {
        System.out.println("Response " + statusCode);
      });
  }
}
