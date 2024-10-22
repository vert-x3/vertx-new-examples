package io.vertx.example.core.net.echossl;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.net.JksOptions;
import io.vertx.core.net.NetServerOptions;
import io.vertx.launcher.application.VertxApplication;

/*
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class Server extends VerticleBase {

  public static void main(String[] args) {
    VertxApplication.main(new String[]{Server.class.getName()});
  }

  @Override
  public Future<?> start() throws Exception {

    NetServerOptions options = new NetServerOptions()
      .setSsl(true)
      .setKeyCertOptions(new JksOptions()
        .setPath("io/vertx/example/core/net/echossl/server-keystore.jks")
        .setPassword("wibble"));

    return vertx
      .createNetServer(options)
      .connectHandler(sock -> {

        // Create a pipe
        sock.pipeTo(sock);

      }).listen(1234)
      .onSuccess(v -> System.out.println("Echo server is now listening"));
  }
}
