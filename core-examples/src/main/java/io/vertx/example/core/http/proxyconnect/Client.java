package io.vertx.example.core.http.proxyconnect;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.net.ProxyOptions;
import io.vertx.core.net.ProxyType;
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
    HttpClientOptions options = new HttpClientOptions()
      .setSsl(true)
      .setTrustAll(true)
      .setVerifyHost(false)
      .setProxyOptions(new ProxyOptions()
        .setType(ProxyType.HTTP)
        .setHost("localhost")
        .setPort(8080));
    client = vertx.createHttpClient(options);
    return client.request(HttpMethod.GET, 8080, "localhost", "/")
      .compose(request -> {
          request.setChunked(true);
          for (int i = 0; i < 10; i++) {
            request.write("client-chunk-" + i);
          }
          request.end();
          return request.response().compose(resp -> {
            System.out.println("Got response " + resp.statusCode());
            return resp.body();
          });
        }
      )
      .onSuccess(body -> System.out.println("Got data " + body.toString("ISO-8859-1")));
  }
}
