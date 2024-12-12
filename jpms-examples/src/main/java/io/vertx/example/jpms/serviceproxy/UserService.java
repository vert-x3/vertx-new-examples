package io.vertx.example.jpms.serviceproxy;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.example.jpms.serviceproxy.impl.SimpleUserService;
import io.vertx.serviceproxy.ServiceProxyBuilder;

/**
 * The service interface.
 */
@ProxyGen
@VertxGen
public interface UserService {

  static void createService(Vertx vertx) {
    vertx
      .eventBus()
      .consumer("user-service", new UserServiceVertxProxyHandler(vertx, new SimpleUserService(vertx)));
  }

  static UserService createProxy(Vertx vertx) {
    return new ServiceProxyBuilder(vertx)
      .setAddress("user-service")
      .build(UserService.class);
  }

  // The service methods
  Future<User> getUser(String userID);

}
