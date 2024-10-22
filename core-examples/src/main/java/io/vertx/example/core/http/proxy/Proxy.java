package io.vertx.example.core.http.proxy;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.launcher.application.VertxApplication;

/*
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class Proxy extends VerticleBase {

  public static void main(String[] args) {
    VertxApplication.main(new String[]{Proxy.class.getName()});
  }

  @Override
  public Future<?> start() throws Exception {
    HttpClient client = vertx.createHttpClient(new HttpClientOptions());
    return vertx.createHttpServer().requestHandler(serverRequest -> {
      System.out.println("Proxying request: " + serverRequest.uri());
      serverRequest.pause();
      HttpServerResponse serverResponse = serverRequest.response();
      client.request(serverRequest.method(), 8282, "localhost", serverRequest.uri())
        .onSuccess(clientRequest -> {
          clientRequest.headers().setAll(serverRequest.headers());
          clientRequest.send(serverRequest).onSuccess(clientResponse -> {
            System.out.println("Proxying response: " + clientResponse.statusCode());
            serverResponse.setStatusCode(clientResponse.statusCode());
            serverResponse.headers().setAll(clientResponse.headers());
            serverResponse.send(clientResponse);
          }).onFailure(err -> {
            System.out.println("Back end failure");
            serverResponse.setStatusCode(500).end();
          });
        }).onFailure(err -> {
          System.out.println("Could not connect to localhost");
          serverResponse.setStatusCode(500).end();
        });
    }).listen(8080);
  }
}
