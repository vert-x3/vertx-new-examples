package io.vertx.example.reactivex.http.client.zip;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
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

  private HttpClient client;

  @Override
  public Completable rxStart() {
    client = vertx.createHttpClient();

    // Send two requests
    Single<JsonObject> req1 = client
      .rxRequest(HttpMethod.GET, 8080, "localhost", "/")
      .flatMap(req -> req
        .rxSend()
        .flatMap(HttpClientResponse::rxBody)
        .map(io.vertx.core.buffer.Buffer::toJsonObject));
    Single<JsonObject> req2 = client
      .rxRequest(HttpMethod.GET, 8080, "localhost", "/")
      .flatMap(req -> req
        .rxSend()
        .flatMap(HttpClientResponse::rxBody)
        .map(io.vertx.core.buffer.Buffer::toJsonObject));

    // Combine the responses with the zip into a single response
    return req1
      .zipWith(req2, (b1, b2) -> new JsonObject().put("req1", b1).put("req2", b2))
      .doOnSuccess(json -> {
        System.out.println("Got combined result " + json);
      }).ignoreElement();
  }
}
