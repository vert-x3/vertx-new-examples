package io.vertx.example.rxjava3.http.server.echo;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.launcher.application.VertxApplication;
import io.vertx.rxjava3.core.AbstractVerticle;
import io.vertx.core.MultiMap;
import io.vertx.rxjava3.core.http.HttpClient;

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

    Flowable<Buffer> payload = Flowable.just(Buffer.buffer("hello", "UTF-8"));

    MultiMap headers = MultiMap.caseInsensitiveMultiMap().add("Content-Type", "text/plain");

    return client
      .rxRequest(HttpMethod.PUT, 8080, "localhost", "/")
      .flatMap(req -> {
        req.headers().addAll(headers);
        return req
          .rxSend(payload)
          .flatMap(resp -> resp.rxBody());
      })
      .doOnSuccess(buf -> System.out.println(buf.toString("UTF-8")))
      .ignoreElement();
  }
}
