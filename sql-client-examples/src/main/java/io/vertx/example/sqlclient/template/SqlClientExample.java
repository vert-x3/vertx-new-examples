package io.vertx.example.sqlclient.template;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.Vertx;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlConnectOptions;
import io.vertx.sqlclient.templates.SqlTemplate;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.Collections;
import java.util.Map;

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

    // create a SQL template for use
    SqlTemplate<Map<String, Object>, RowSet<Row>> template = SqlTemplate
      .forQuery(pool, "select * from test where id = #{id}");

    // create a test table
    return pool.query("create table test(id int primary key, name varchar(255))")
      .execute()
      .compose(r ->
        // insert some test data
        pool
          .query("insert into test values (1, 'Hello'), (2, 'World')")
          .execute()
      ).compose(r ->
      // query some data with arguments
      template.execute(Collections.singletonMap("id", 2))
    ).onSuccess(rows -> {
      for (Row row : rows) {
        System.out.println("row = " + row.toJson());
      }
    });
  }
}
