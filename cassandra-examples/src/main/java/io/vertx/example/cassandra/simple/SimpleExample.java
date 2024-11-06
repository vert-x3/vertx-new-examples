package io.vertx.example.cassandra.simple;

import com.datastax.oss.driver.api.core.cql.Row;
import io.vertx.cassandra.CassandraClient;
import io.vertx.cassandra.CassandraClientOptions;
import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.launcher.application.VertxApplication;
import org.testcontainers.cassandra.CassandraContainer;

public class SimpleExample extends VerticleBase {

  private final static CassandraContainer CASSANDRA_CONTAINER = new CassandraContainer("cassandra:3.11.2");

  /**
   * Convenience method so you can run it in your IDE
   */
  public static void main(String[] args) {
    CASSANDRA_CONTAINER.start();
    VertxApplication.main(new String[]{SimpleExample.class.getName()});
  }

  @Override
  public Future<?> start() {
    CassandraClientOptions options = new CassandraClientOptions()
      .addContactPoint(CASSANDRA_CONTAINER.getContactPoint())
      .setUsername(CASSANDRA_CONTAINER.getUsername())
      .setPassword(CASSANDRA_CONTAINER.getPassword());
    options
      .dataStaxClusterBuilder()
      .withLocalDatacenter("datacenter1");
    CassandraClient client = CassandraClient.create(vertx, options);

    return client.execute("select release_version from system.local")
      .onSuccess(res -> {
        Row row = res.one();
        String releaseVersion = row.getString("release_version");
        System.out.println("Release version is: " + releaseVersion);
    });
  }
}
