package io.vertx.example.sqlclient.transaction;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.Vertx;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.SqlConnectOptions;
import org.testcontainers.containers.PostgreSQLContainer;

/*
 * @author <a href="mailto:pmlopes@gmail.com">Paulo Lopes</a>
 */
public class SqlClientExample extends VerticleBase {

  // Convenience method so you can run it in your IDE
  public static void main(String[] args) {
    PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>();
    postgres.start();
    PgConnectOptions options = new PgConnectOptions()
      .setPort(postgres.getMappedPort(5432))
      .setHost(postgres.getContainerIpAddress())
      .setDatabase(postgres.getDatabaseName())
      .setUser(postgres.getUsername())
      .setPassword(postgres.getPassword());
    // Uncomment for MySQL
//    MySQLContainer<?> mysql = new MySQLContainer<>();
//    mysql.start();
//    MySQLConnectOptions options = new MySQLConnectOptions()
//      .setPort(mysql.getMappedPort(3306))
//      .setHost(mysql.getContainerIpAddress())
//      .setDatabase(mysql.getDatabaseName())
//      .setUser(mysql.getUsername())
//      .setPassword(mysql.getPassword());
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new SqlClientExample(options));
  }

  private final SqlConnectOptions options;
  private Pool pool;

  public SqlClientExample(SqlConnectOptions options) {
    this.options = options;
  }

  @Override
  public Future<?> start() {
    pool = Pool.pool(vertx, options, new PoolOptions().setMaxSize(4));

    return pool.withTransaction(sqlClient -> {
      // create a test table
      return sqlClient.query("create table test(id int primary key, name varchar(255))").execute()
        .compose(v -> {
          // insert some test data
          return sqlClient.query("insert into test values (1, 'Hello'), (2, 'World')").execute();
        })
        .compose(v -> {
          // query some data
          return sqlClient.query("select * from test").execute();
        });
    }) .onSuccess(rows -> {
      for (Row row : rows) {
        System.out.println("row = " + row.toJson());
      }
    });
  }
}
