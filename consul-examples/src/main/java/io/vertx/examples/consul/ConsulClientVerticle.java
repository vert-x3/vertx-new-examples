package io.vertx.examples.consul;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.ext.consul.ConsulClient;
import io.vertx.launcher.application.VertxApplication;

/**
 * @author <a href="mailto:ruslan.sennov@gmail.com">Ruslan Sennov</a>
 */
public class ConsulClientVerticle extends VerticleBase {

  /**
   * Convenience method so you can run it in your IDE
   */
  public static void main(String[] args) {
    VertxApplication.main(new String[]{ConsulClientVerticle.class.getName()});
  }

  private ConsulClient consulClient;

  @Override
  public Future<?> start() {
    consulClient = ConsulClient.create(vertx);
    return consulClient.putValue("key11", "value11")
      .compose(v -> {
        System.out.println("KV pair saved");
        return consulClient.getValue("key11");
      }).onSuccess(ar -> {
        System.out.println("KV pair retrieved");
    });
  }
}
