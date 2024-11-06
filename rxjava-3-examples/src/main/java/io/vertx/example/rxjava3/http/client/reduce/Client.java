package io.vertx.example.rxjava3.http.client.reduce;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.launcher.application.VertxApplication;
import io.vertx.rxjava3.core.AbstractVerticle;
import io.vertx.rxjava3.core.http.HttpClient;

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

    Maybe<String> maybe = client.rxRequest(HttpMethod.GET, 8080, "localhost", "/")

      // Connect
      .flatMapMaybe(req -> req

        // Send request
        .rxSend()

        // Status code check and -> Observable<Buffer>
        .flatMapPublisher(resp -> {
          if (resp.statusCode() != 200) {
            return Flowable.error(new RuntimeException("Wrong status code " + resp.statusCode()));
          }
          return Flowable.just(Buffer.buffer()).mergeWith(resp.toFlowable());
        })

        // Reduce all buffers in a single buffer
        .reduce(Buffer::appendBuffer)

        // Turn in to a string
        .map(buffer -> buffer.toString("UTF-8"))
      );

    return maybe
      .doOnSuccess(data -> System.out.println("Server content " + data))
      .ignoreElement();
  }
}
