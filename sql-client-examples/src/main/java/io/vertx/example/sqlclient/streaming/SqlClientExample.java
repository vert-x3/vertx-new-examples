package io.vertx.example.sqlclient.streaming;

import io.vertx.core.Future;
import io.vertx.core.Promise;
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

    return pool.withConnection(connection -> {
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
        .compose(v ->  connection
          .prepare("select * from test")
          .compose(ps -> {
            RowStream<Row> stream = ps.createStream(50);
            Promise<Void> promise = Promise.promise();
            stream
              .exceptionHandler(promise::fail)
              .endHandler(promise::complete)
              .handler(row -> System.out.println("row = " + row.toJson()));
            return promise
              .future()
              .eventually(ps::close);
          }));
    }).onSuccess(ar -> {
      System.out.println("done");
    });
  }
}
