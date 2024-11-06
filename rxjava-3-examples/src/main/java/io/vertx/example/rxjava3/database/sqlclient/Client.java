package io.vertx.example.rxjava3.database.sqlclient;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.vertx.jdbcclient.JDBCConnectOptions;
import io.vertx.launcher.application.VertxApplication;
import io.vertx.rxjava3.core.AbstractVerticle;
import io.vertx.rxjava3.jdbcclient.JDBCPool;
import io.vertx.rxjava3.sqlclient.Pool;
import io.vertx.rxjava3.sqlclient.Row;
import io.vertx.rxjava3.sqlclient.RowSet;
import io.vertx.sqlclient.PoolOptions;

/*
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class Client extends AbstractVerticle {

  public static void main(String[] args) {
    VertxApplication.main(new String[]{Client.class.getName()});
  }

  @Override
  public Completable rxStart() {

    Pool pool = JDBCPool.pool(vertx, new JDBCConnectOptions().setJdbcUrl("jdbc:hsqldb:mem:test?shutdown=true"), new PoolOptions());

    Maybe<RowSet<Row>> resa = pool.rxWithConnection(conn -> conn
      .query("CREATE TABLE test(col VARCHAR(20))")
      .rxExecute()
      .flatMap(res -> conn.query("INSERT INTO test (col) VALUES ('val1')").rxExecute())
      .flatMap(res -> conn.query("INSERT INTO test (col) VALUES ('val2')").rxExecute())
      .flatMap(res -> conn.query("SELECT * FROM test").rxExecute())
      .toMaybe());

    // Connect to the database
    return resa
      .doOnSuccess(rowSet -> {
        // Subscribe to the final result
        System.out.println("Results:");
        rowSet.forEach(row -> {
          System.out.println(row.toJson());
        });
      })
      .ignoreElement();
  }
}
