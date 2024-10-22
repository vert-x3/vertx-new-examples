package io.vertx.example.core.verticle.asyncstart;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;

/*
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class OtherVerticle extends VerticleBase {

  @Override
  public Future<?> start() throws Exception {

    System.out.println("In OtherVerticle.start (async)");

    // This verticle takes some time to start (maybe it has to deploy other verticles or whatever)
    // So we override the async version of start(), then we can mark the verticle as started some time later
    // when all the slow startup is done, without blocking the actual start method.

    // We simulate this long startup time by setting a timer
    return vertx
      .timer(2000)
      .andThen(v -> {

        // Now everything is started, we can tell Vert.x this verticle is started then it will call the deploy handler
        // of the caller that originally deployed it

        System.out.println("Startup tasks are now complete, OtherVerticle is now started!");

      });
  }

  @Override
  public Future<?> stop() throws Exception {

    // If you have slow cleanup tasks to perform, you can similarly override the async stop method

    return vertx
      .timer(2000)
      .andThen(tid -> {

        System.out.println("Cleanup tasks are now complete, OtherVerticle is now stopped!");
      });
  }
}
