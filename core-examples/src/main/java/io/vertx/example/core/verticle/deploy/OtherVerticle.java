package io.vertx.example.core.verticle.deploy;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;

/*
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class OtherVerticle extends VerticleBase {

  @Override
  public Future<?> start() throws Exception {

    // The start method will be called when the verticle is deployed

    System.out.println("In OtherVerticle.start");

    System.out.println("Config is " + config());

    return super.start();
  }

  @Override
  public Future<?> stop() throws Exception {

    // You can optionally override the stop method too, if you have some clean-up to do

    System.out.println("In OtherVerticle.stop");

    return super.stop();
  }
}
