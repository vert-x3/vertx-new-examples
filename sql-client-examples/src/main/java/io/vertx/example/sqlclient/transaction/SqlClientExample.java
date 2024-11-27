package io.vertx.example.sqlclient.transaction;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.SqlConnectOptions;

/*
 * @author <a href="mailto:pmlopes@gmail.com">Paulo Lopes</a>
 */
public abstract class SqlClientExample extends VerticleBase {

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
