package io.vertx.example.core.http.sharing;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.launcher.application.VertxApplication;

/**
 * An example illustrating the server sharing and round robin. The servers are identified using an id.
 * The HTTP Server Verticle is instantiated twice in the deployment options.
 */
public class Server extends VerticleBase {

  public static void main(String[] args) {
    VertxApplication.main(new String[]{Server.class.getName()});
  }

  @Override
  public Future<?> start() throws Exception {
    return vertx.deployVerticle(
        "io.vertx.example.core.http.sharing.HttpServerVerticle",
        new DeploymentOptions().setInstances(2));
  }
}
