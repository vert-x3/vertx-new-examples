package io.vertx.example.cassandra.streaming;

import io.vertx.cassandra.CassandraClient;
import io.vertx.cassandra.CassandraClientOptions;
import io.vertx.cassandra.CassandraRowStream;
import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.launcher.application.VertxApplication;
import org.testcontainers.cassandra.CassandraContainer;

public class StreamingExample extends VerticleBase {

  private final static CassandraContainer CASSANDRA_CONTAINER = new CassandraContainer("cassandra:3.11.2");

  /**
   * Convenience method so you can run it in your IDE
   */
  public static void main(String[] args) {
    CASSANDRA_CONTAINER.start();
    VertxApplication.main(new String[]{StreamingExample.class.getName()});
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

    return Future.future(p -> {
      client
        .queryStream("SELECT * from system_schema.tables  WHERE keyspace_name = 'system_schema' ")
        .onComplete(cassandraRowStreamAsyncResult -> {
          if (cassandraRowStreamAsyncResult.succeeded()) {
            System.out.println("Tables in system_schema: ");
            CassandraRowStream stream = cassandraRowStreamAsyncResult.result();
            stream
              .endHandler(end -> {
                p.tryComplete();
              })
              .handler(row -> {
                String systemSchema = row.getString("table_name");
                System.out.println("\t" + systemSchema);
              })
              .exceptionHandler(throwable -> {
                System.out.println("An exception occurred:");
                p.tryFail(cassandraRowStreamAsyncResult.cause());
              });
          } else {
            System.out.println("Unable to execute the query");
            p.tryFail(cassandraRowStreamAsyncResult.cause());
          }
        });
    });
  }
}
