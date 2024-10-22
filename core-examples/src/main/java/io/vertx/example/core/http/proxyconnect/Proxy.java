package io.vertx.example.core.http.proxyconnect;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetSocket;
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

    NetClient client = vertx.createNetClient(new NetClientOptions());

    return vertx
      .createHttpServer()
      .requestHandler(req -> {
        if (req.method() == HttpMethod.CONNECT) {

          // Determine proxied server address
          String proxyAddress = req.uri();
          int idx = proxyAddress.indexOf(':');
          String host = proxyAddress.substring(0, idx);
          int port = Integer.parseInt(proxyAddress.substring(idx + 1));

          System.out.println("Connecting to proxy " + proxyAddress);
          client.connect(port, host).onComplete(ar -> {

            if (ar.succeeded()) {
              System.out.println("Connected to proxy");

              NetSocket serverSocket = ar.result();
              serverSocket.pause();

              req.toNetSocket().onComplete(ar2 -> {
                if (ar2.succeeded()) {
                  NetSocket clientSocket = ar2.result();
                  serverSocket.handler(buff -> {
                    System.out.println("Forwarding server packet to the client");
                    clientSocket.write(buff);
                  });
                  serverSocket.closeHandler(v -> {
                    System.out.println("Server socket closed");
                    clientSocket.close();
                  });
                  clientSocket.handler(buff -> {
                    System.out.println("Forwarding client packet to the server");
                    serverSocket.write(buff);
                  });
                  clientSocket.closeHandler(v -> {
                    System.out.println("Client socket closed");
                    serverSocket.close();
                  });
                  serverSocket.resume();
                } else {
                  serverSocket.close();
                }
              });
            } else {

              System.out.println("Fail proxy connection");
              req.response().setStatusCode(403).end();
            }
          });
        } else {
          req.response().setStatusCode(405).end();
        }
      }).listen(8080);
  }
}
