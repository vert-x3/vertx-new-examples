package io.vertx.example.virtualthreads;

import io.vertx.core.*;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.sqlclient.*;
import org.testcontainers.containers.PostgreSQLContainer;

public class SqlClientExample extends VerticleBase {

  public static void main(String[] args) throws Exception {
    var vertx = Vertx.vertx();
    try (var container = new PostgreSQLContainer<>()) {
      container.start();
      vertx.deployVerticle(() -> {
          PgConnectOptions connectOptions = new PgConnectOptions()
            .setPort(container.getMappedPort(5432))
            .setHost(container.getContainerIpAddress())
            .setDatabase(container.getDatabaseName())
            .setUser(container.getUsername())
            .setPassword(container.getPassword());
          return new SqlClientExample(Pool.pool(vertx, connectOptions, new PoolOptions().setMaxSize(4)));
        }, new DeploymentOptions()
          .setThreadingModel(ThreadingModel.VIRTUAL_THREAD))
        .await();
    }
  }

  private final Pool pool;

  public SqlClientExample(Pool pool) {
    this.pool = pool;
  }

  @Override
  public Future<?> start() throws Exception {
    // create a test table
    pool.query("create table test(id int primary key, name varchar(255))").execute().await();
    // insert some test data
    pool.query("insert into test values (1, 'Hello'), (2, 'World')").execute().await();
    // query some data
    var rows = pool.query("select * from test").execute().await();
    for (Row row : rows) {
      System.out.println("row = " + row.toJson());
    }
    return super.start();
  }
}
