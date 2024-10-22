package io.vertx.example.core.net.echo;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
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

    return vertx
      .createNetServer()
      .connectHandler(sock -> {

        // Create a pipe
        sock.pipeTo(sock);

      }).listen(1234)
      .onSuccess(v -> System.out.println("Echo server is now listening"));
  }
}
