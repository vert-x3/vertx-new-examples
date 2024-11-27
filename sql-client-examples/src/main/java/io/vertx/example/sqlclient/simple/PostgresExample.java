package io.vertx.example.sqlclient.simple;

import io.vertx.core.Vertx;
import io.vertx.example.sqlclient.PostgresServerHelper;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.sqlclient.SqlConnectOptions;

public class PostgresExample extends SqlClientExample {

  // Convenience method so you can run it in your IDE
  public static void main(String[] args) {
    PostgresServerHelper dbHelper = new PostgresServerHelper();
    PgConnectOptions connectOptions = dbHelper.startDb();
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new PostgresExample(connectOptions)).await();
  }

  public PostgresExample(SqlConnectOptions options) {
    super(options);
  }
}
