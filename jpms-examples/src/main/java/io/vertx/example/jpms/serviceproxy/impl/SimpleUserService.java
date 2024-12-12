package io.vertx.example.jpms.serviceproxy.impl;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.example.jpms.serviceproxy.User;
import io.vertx.example.jpms.serviceproxy.UserService;

import java.util.concurrent.TimeUnit;

public class SimpleUserService implements UserService {

  private final Vertx vertx;

  public SimpleUserService(Vertx vertx) {
    this.vertx = vertx;
  }

  @Override
  public Future<User> getUser(String userName) {
    return vertx.timer(10, TimeUnit.MILLISECONDS).map(v -> new User()
      .setFirstName("Dale")
      .setLastName("Cooper")
    );
  }
}
