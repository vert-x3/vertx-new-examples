package io.vertx.example.reactivex.database.sqlclient;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.functions.Function;
import io.vertx.jdbcclient.JDBCConnectOptions;
import io.vertx.launcher.application.VertxApplication;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.jdbcclient.JDBCPool;
import io.vertx.reactivex.sqlclient.Pool;
import io.vertx.reactivex.sqlclient.Row;
import io.vertx.reactivex.sqlclient.RowSet;
import io.vertx.reactivex.sqlclient.SqlConnection;
import io.vertx.sqlclient.PoolOptions;

/*
 * @author <a href="mailto:plopes@redhat.com">Paulo Lopes</a>
 */
public class Streaming extends AbstractVerticle {

  public static void main(String[] args) {
    VertxApplication.main(new String[]{Streaming.class.getName()});
  }

  @Override
  public Completable rxStart() {

    Pool jdbc = JDBCPool.pool(vertx, new JDBCConnectOptions().setJdbcUrl("jdbc:hsqldb:mem:test?shutdown=true"), new PoolOptions());

    // Connect to the database
    Maybe<RowSet<Row>> maybe = jdbc.rxWithConnection((Function<SqlConnection, Maybe<RowSet<Row>>>) conn -> {
      // With the connection...
      Single<RowSet<Row>> single = conn
        // ...create test table
        .query("CREATE TABLE test(col VARCHAR(20))").rxExecute()
        // ...insert a row
        .flatMap(result -> conn.query("INSERT INTO test (col) VALUES ('val1')").rxExecute())
        // ...another one
        .flatMap(result -> conn.query("INSERT INTO test (col) VALUES ('val2')").rxExecute())
        // ...get values
        .flatMap(result -> conn.query("SELECT * FROM test").rxExecute());
      return Maybe.fromSingle(single);
    });

    return maybe.doOnSuccess(rows -> {
      for (Row row : rows) {
        System.out.println("Row : " + row.toJson().encode());
      }
    }).ignoreElement();
  }
}
