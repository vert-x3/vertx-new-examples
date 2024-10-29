package io.vertx.example.reactivex.services.serviceproxy;


import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.launcher.application.VertxApplication;
import io.vertx.serviceproxy.ServiceBinder;

public class SomeDatabaseServiceVerticle extends VerticleBase {
  SomeDatabaseService someDatabaseService;

  public static void main(String[] args) {
    VertxApplication.main(new String[]{SomeDatabaseServiceVerticle.class.getName(), "-cluster"});
  }

  @Override
  public Future<?> start() throws Exception {
    // Use Factory method or just with constructor, either is OK
    someDatabaseService = SomeDatabaseService.create();

    // Register your service to the address.
    return new ServiceBinder(vertx)
      .setAddress("proxy.address")
      .register(SomeDatabaseService.class, someDatabaseService)
      .completion();
  }
}
