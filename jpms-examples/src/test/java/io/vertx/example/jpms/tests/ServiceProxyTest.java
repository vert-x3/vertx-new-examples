package io.vertx.example.jpms.tests;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.example.jpms.serviceproxy.User;
import io.vertx.example.jpms.serviceproxy.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ServiceProxyTest {

  private Vertx vertx;
  private UserService proxy;

  @BeforeEach
  public void setup() {
    vertx = Vertx.vertx();
    proxy = UserService.createProxy(vertx);
    UserService.createService(vertx);
  }

  @AfterEach
  public void tearDown() {
    vertx
      .close()
      .await();
  }

  @Test
  public void testProxy() {
    User result = proxy.getUser("agent").await();
    assertNotNull(result);
    assertEquals("Dale", result.getFirstName());
    assertEquals("Cooper", result.getLastName());
  }
}
