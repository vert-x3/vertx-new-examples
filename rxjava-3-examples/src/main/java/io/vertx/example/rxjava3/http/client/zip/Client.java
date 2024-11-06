package io.vertx.example.rxjava3.http.client.zip;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.launcher.application.VertxApplication;
import io.vertx.rxjava3.core.AbstractVerticle;
import io.vertx.rxjava3.core.http.HttpClient;
import io.vertx.rxjava3.core.http.HttpClientResponse;

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

    // Send two requests
    Single<JsonObject> req1 = client
      .rxRequest(HttpMethod.GET, 8080, "localhost", "/")
      .flatMap(req -> req
        .rxSend()
        .flatMap(HttpClientResponse::rxBody)
        .map(Buffer::toJsonObject));
    Single<JsonObject> req2 = client
      .rxRequest(HttpMethod.GET, 8080, "localhost", "/")
      .flatMap(req -> req
        .rxSend()
        .flatMap(HttpClientResponse::rxBody)
        .map(Buffer::toJsonObject));

    // Combine the responses with the zip into a single response
    return req1
      .zipWith(req2, (b1, b2) -> new JsonObject().put("req1", b1).put("req2", b2))
      .doOnSuccess(json -> System.out.println("Got combined result " + json))
      .ignoreElement();
  }
}
