package io.vertx.example.core.http.connect;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.http.*;
import io.vertx.launcher.application.VertxApplication;

/*
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class Client extends VerticleBase {

  public static void main(String[] args) {
    VertxApplication.main(new String[]{Client.class.getName()});
  }

  private HttpClientAgent client;

  @Override
  public Future<?> start() throws Exception {
    client = vertx.createHttpClient();

    Future<HttpClientConnection> fut = client.connect(new HttpConnectOptions().setPort(8080).setHost("localhost"));

    return fut
      .compose(conn -> {
        System.out.println("Obtained connection to server");
        return conn
          // Once we obtained a connection we can use it
          .request(HttpMethod.GET, 8080, "localhost", "/")
          .compose(req -> req
            .send()
            .compose(resp -> {
              System.out.println("Got response " + resp.statusCode());
              return resp.body();
            }))
          // Since the connection is not managed by the pool, we should close it after usage
          .eventually(() -> {
            System.out.println("Closing connection");
            return conn.close();
          });
      })
      .onSuccess(body -> System.out.println("Got data " + body.toString("ISO-8859-1")));
  }
}
