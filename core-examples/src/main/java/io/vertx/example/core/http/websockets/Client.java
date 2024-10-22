package io.vertx.example.core.http.websockets;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.WebSocketClient;
import io.vertx.launcher.application.VertxApplication;

/*
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class Client extends VerticleBase {

  public static void main(String[] args) {
    VertxApplication.main(new String[]{Client.class.getName()});
  }

  private WebSocketClient client;

  @Override
  public Future<?> start() throws Exception {
    client = vertx.createWebSocketClient();

    return client
      .webSocket()
      .handler(data -> {
        System.out.println("Received data " + data.toString("ISO-8859-1"));
        client.close();
        client = null;
      }).connect(8080, "localhost", "/some-uri")
      .onSuccess(webSocket -> {
        webSocket.writeBinaryMessage(Buffer.buffer("Hello world"));
      });
  }
}
