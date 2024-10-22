package io.vertx.example.core.http2.push;

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

  private HttpClient client;

  @Override
  public Future<?> start() throws Exception {

    // Note! in real-life you wouldn't often set trust all to true as it could leave you open to man in the middle attacks.

    HttpClientOptions options = new HttpClientOptions().
      setSsl(true).
      setUseAlpn(true).
      setProtocolVersion(HttpVersion.HTTP_2).
      setTrustAll(true);

    client = vertx.createHttpClient(options);

    return client.request(HttpMethod.GET, 8443, "localhost", "/").compose(request -> {

        // Set handler for server side push
        request.pushHandler(pushedReq -> {
          System.out.println("Receiving pushed content");
          pushedReq.response().compose(HttpClientResponse::body).onSuccess(body -> {
            System.out.println("Got pushed data " + body.toString("ISO-8859-1"));
          });
        });

        return request.send().compose(resp -> {
          System.out.println("Got response " + resp.statusCode() + " with protocol " + resp.version());
          return resp.body();
        });
      })
      .onSuccess(body -> {
        System.out.println("Got data " + body.toString("ISO-8859-1"));
      });
  }
}
