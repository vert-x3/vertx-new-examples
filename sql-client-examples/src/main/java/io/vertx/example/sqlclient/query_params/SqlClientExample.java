package io.vertx.example.sqlclient.query_params;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.sqlclient.*;

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
      pool
        .preparedQuery(selectQuery())
        .execute(Tuple.of(2))
    ).onSuccess(rows -> {
      for (Row row : rows) {
        System.out.println("row = " + row.toJson());
      }
    });
  }

  protected abstract String selectQuery();
}
