package io.vertx.example.jpms.tests;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClientAgent;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpResponseExpectation;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.example.jpms.sqlclient.Client;
import io.vertx.pgclient.PgConnectOptions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@EnabledOnOs(value = { OS.LINUX })
public class SqlClientTest {

  private static GenericContainer<?> container;
  private static PgConnectOptions database;

  @BeforeAll
  public static void startPostgres() {
    container = new GenericContainer(DockerImageName.parse("postgres").withTag("9.6.12")) {
      @Override
      protected void configure() {
        this.addEnv("POSTGRES_DB", "postgres");
        this.addEnv("POSTGRES_USER", "postgres");
        this.addEnv("POSTGRES_PASSWORD", "postgres");
      }
    }
    .withClasspathResourceMapping("periodic_table.sql", "/docker-entrypoint-initdb.d/create-postgres.sql", BindMode.READ_ONLY);
    container.addExposedPort(5432);
    container.start();
    database = new PgConnectOptions()
      .setHost(container.getHost())
      .setPort(container.getMappedPort(5432))
      .setDatabase("postgres")
      .setUser("postgres")
      .setPassword("postgres");
  }

  @AfterAll
  public static void stopPostgres() {
    if (container != null) {
      try {
        container.stop();
      } finally {
        container = null;
      }
    }
  }

  private Vertx vertx;
  private HttpClientAgent client;

  @BeforeEach
  public void beforeEach() throws Exception {
    vertx = Vertx.vertx();
    vertx.deployVerticle(new Client(database))
      .toCompletionStage()
      .toCompletableFuture()
      .get(20, TimeUnit.SECONDS);
    client = vertx.createHttpClient();
  }

  @AfterEach
  public void afterEach() {
    if (vertx != null) {
      try {
        vertx.close();
      } finally {
        vertx = null;
      }
    }
  }

  @Test
  public void testClient() throws Exception {
    JsonArray result = client
      .request(HttpMethod.GET, 8080, "localhost", "/")
      .compose(req -> req.send()
        .expecting(HttpResponseExpectation.SC_OK)
        .expecting(HttpResponseExpectation.JSON)
        .compose(HttpClientResponse::body)
        .map(Buffer::toJsonArray)
      )
      .toCompletionStage()
      .toCompletableFuture()
      .get(20, TimeUnit.SECONDS);
    Optional<JsonObject> hydrogen = result.stream()
      .map(o -> ((JsonObject) o))
      .filter(elt -> "Hydrogen".equals(elt.getString("Element")))
      .findFirst();
    Assertions.assertTrue(hydrogen.isPresent());
    Assertions.assertEquals("H", hydrogen.get().getString("Symbol"));
  }
}
