package io.vertx.example.reactivex.http.client.simple;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.launcher.application.VertxApplication;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.http.HttpClient;

/*
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class Client extends AbstractVerticle {

  public static void main(String[] args) {
    VertxApplication.main(new String[]{Client.class.getName()});
  }

  @Override
  public Completable rxStart() {
    HttpClient client = vertx.createHttpClient();
    Single<Buffer> single = client
      .rxRequest(HttpMethod.GET, 8080, "localhost", "/")
      .flatMap(req -> req
        .rxSend()
        .flatMap(resp -> {
          // Status code check and -> Single<Buffer>
          if (resp.statusCode() != 200) {
            return Single.error(new RuntimeException("Wrong status code " + resp.statusCode()));
          }
          return resp.rxBody();
        }));
    return single
      .doOnSuccess(data -> System.out.println("Server content " + data.toString("UTF-8")))
      .ignoreElement();
  }
}
