package io.vertx.example.sqlclient.transaction_rollback;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.SqlConnectOptions;

/*
 * @author <a href="mailto:pmlopes@gmail.com">Paulo Lopes</a>
 */
public class SqlClientExample extends VerticleBase {

  private final SqlConnectOptions options;
  private Pool pool;

  public SqlClientExample(SqlConnectOptions options) {
    this.options = options;
  }

  @Override
  public Future<?> start() {

    pool = Pool.pool(vertx, options, new PoolOptions().setMaxSize(4));

    // expected
    return pool.withTransaction(connection -> {
      // create a test table
      return connection
        .query("create table test(id int primary key, name varchar(255))")
        .execute()
        .compose(v -> {
          // insert some test data
          return connection
            .query("insert into test values (1, 'Hello'), (2, 'World')")
            .execute();
        })
        .compose(v -> {
          // triggers transaction roll back
          System.out.println("ROLLING BACK");
          return Future.failedFuture("Expected failure");
        })
        .compose(v -> {
          // query some data
          // should not be called because of rollback
          return connection.query("select * from test").execute();
        });
    }).onFailure(err -> {
      System.out.println("Transaction rolled back");
    });
  }
}
