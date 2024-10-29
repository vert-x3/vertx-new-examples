package io.vertx.example.reactivex.services.serviceproxy;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import io.vertx.launcher.application.VertxApplication;
import io.vertx.reactivex.core.AbstractVerticle;

public class ServiceConsumerVerticle extends AbstractVerticle {
  // In order to use Rx-ified methods, you need to declare the service in Rx-style.
  io.vertx.example.reactivex.services.serviceproxy.reactivex.SomeDatabaseService someDatabaseService;

  public static void main(String[] args) {
    VertxApplication.main(new String[]{ServiceConsumerVerticle.class.getName(), "-cluster"});
  }

  @Override
  public Completable rxStart() {
    someDatabaseService = SomeDatabaseService.createProxy(vertx.getDelegate(), "proxy.address");

    int id = 1;

    // Now you can use your Rx-ified methods.
    Single<JsonObject> single = someDatabaseService.rxGetDataById(id);

    return single
      .doOnSuccess(jsonObject -> System.out.println(jsonObject.encodePrettily()))
      .ignoreElement();
  }
}
