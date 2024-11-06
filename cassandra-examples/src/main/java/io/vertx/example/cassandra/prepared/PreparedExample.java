package io.vertx.example.cassandra.prepared;

import io.vertx.cassandra.CassandraClient;
import io.vertx.cassandra.CassandraClientOptions;
import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.launcher.application.VertxApplication;
import org.testcontainers.cassandra.CassandraContainer;

public class PreparedExample extends VerticleBase {

  private final static CassandraContainer CASSANDRA_CONTAINER = new CassandraContainer("cassandra:3.11.2");

  /**
   * Convenience method so you can run it in your IDE
   */
  public static void main(String[] args) {
    CASSANDRA_CONTAINER.start();
    VertxApplication.main(new String[]{PreparedExample.class.getName()});
  }

  private CassandraClient client;

  @Override
  public Future<?> start() {
    CassandraClientOptions options = new CassandraClientOptions()
      .addContactPoint(CASSANDRA_CONTAINER.getContactPoint())
      .setUsername(CASSANDRA_CONTAINER.getUsername())
      .setPassword(CASSANDRA_CONTAINER.getPassword());
    options
      .dataStaxClusterBuilder()
      .withLocalDatacenter("datacenter1");
    client = CassandraClient.create(vertx, options);

    return client
      .prepare("SELECT * from system_schema.tables  WHERE keyspace_name = ? ")
      .compose(preparedStatement -> client.executeWithFullFetch(preparedStatement.bind("system_schema")))
      .onSuccess(res -> {
        System.out.println("Tables in system_schema: ");
        res.forEach(row -> {
          String systemSchema = row.getString("table_name");
          System.out.println("\t" + systemSchema);
        });
    });
  }
}
