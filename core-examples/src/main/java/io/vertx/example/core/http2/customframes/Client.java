package io.vertx.example.core.http2.customframes;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpVersion;
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

    client.request(HttpMethod.GET, 8443, "localhost", "/")
      .onSuccess(request -> {
        request.response().onSuccess(resp -> {

          // Print custom frames received from server
          resp.customFrameHandler(frame -> {
            System.out.println("Got frame from server " + frame.payload().toString("UTF-8"));
          });
        });
        request.sendHead().onSuccess(v -> {

          // Once head has been sent we can send custom frames
          vertx.setPeriodic(1000, timerID -> {

            System.out.println("Sending ping frame to server");
            request.writeCustomFrame(10, 0, Buffer.buffer("ping"));
          });
        });
      });

    return super.start();
  }
}
