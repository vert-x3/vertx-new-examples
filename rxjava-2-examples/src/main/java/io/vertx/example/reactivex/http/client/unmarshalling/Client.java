package io.vertx.example.reactivex.http.client.unmarshalling;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.Json;
import io.vertx.launcher.application.VertxApplication;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.http.HttpClient;
import io.vertx.reactivex.core.http.HttpClientResponse;

/*
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class Client extends AbstractVerticle {

  public static void main(String[] args) {
    VertxApplication.main(new String[]{Client.class.getName()});
  }

  // Unmarshalled response from server
  static class Data {

    public String message;

  }

  @Override
  public Completable rxStart() {
    HttpClient client = vertx.createHttpClient();

    Single<Data> flowable = client
      .rxRequest(HttpMethod.GET, 8080, "localhost", "/")
      .flatMap(req -> req
        .rxSend()
        .flatMap(HttpClientResponse::rxBody)
        // Unmarshall the response to the Data object via Jackon
        .map(buffer -> Json.decodeValue(buffer, Data.class))
      );

    return flowable
      .doOnSuccess(data -> System.out.println("Got response " + data.message))
      .ignoreElement();
  }
}
