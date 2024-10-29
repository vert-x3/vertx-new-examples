package io.vertx.example.jpms.tests;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpResponseExpectation;
import io.vertx.core.json.JsonObject;
import io.vertx.example.jpms.native_transport.Server;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NativeTransportTest {

  private Vertx vertx;

  @BeforeEach
  public void before() {
    vertx = Vertx.vertx(new VertxOptions().setPreferNativeTransport(true));
    assertTrue(vertx.isNativeTransportEnabled());
  }

  @AfterEach
  public void after() {
    vertx
      .close()
      .await();
  }

  @Test
  public void testNativeTransport() {
    vertx
      .deployVerticle(new Server())
      .await();
    HttpClient client = vertx.createHttpClient();
    JsonObject res = client.request(HttpMethod.GET, 8080, "localhost", "/")
      .compose(req -> req.send()
        .expecting(HttpResponseExpectation.SC_OK)
        .compose(resp -> resp
          .body()
          .map(Buffer::toJsonObject))
      )
      .await();
    assertEquals("Hello World", res.getString("message"));
    assertTrue(res.getBoolean("nativeTransport"));
  }
}
