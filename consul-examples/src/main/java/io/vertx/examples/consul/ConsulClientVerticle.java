package io.vertx.examples.consul;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.ext.consul.ConsulClient;
import io.vertx.ext.consul.ConsulClientOptions;
import io.vertx.launcher.application.VertxApplication;

import org.testcontainers.consul.ConsulContainer;

/**
 * @author <a href="mailto:ruslan.sennov@gmail.com">Ruslan Sennov</a>
 */
public class ConsulClientVerticle extends VerticleBase {

  private static final ConsulContainer CONSUL_CONTAINER = new ConsulContainer("hashicorp/consul:1.15");

  /**
   * Convenience method so you can run it in your IDE
   */
  public static void main(String[] args) {
    CONSUL_CONTAINER.start();
    VertxApplication.main(new String[]{ConsulClientVerticle.class.getName()});
  }

  private ConsulClient consulClient;

  @Override
  public Future<?> start() {
    ConsulClientOptions options = new ConsulClientOptions();
    options.setHost(CONSUL_CONTAINER.getHost());
    options.setPort(CONSUL_CONTAINER.getFirstMappedPort());
    consulClient = ConsulClient.create(vertx, options);

    return consulClient.putValue("key11", "value11")
      .compose(v -> {
        System.out.println("KV pair saved");
        return consulClient.getValue("key11");
      }).onSuccess(ar -> {
        System.out.println("KV pair retrieved");
    });
  }
}
