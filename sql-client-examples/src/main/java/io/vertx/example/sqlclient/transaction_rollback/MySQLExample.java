package io.vertx.example.sqlclient.transaction_rollback;

import io.vertx.core.Vertx;
import io.vertx.example.sqlclient.MySQLServerHelper;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.sqlclient.SqlConnectOptions;

public class MySQLExample extends SqlClientExample {

  // Convenience method so you can run it in your IDE
  public static void main(String[] args) {
    MySQLServerHelper dbHelper = new MySQLServerHelper();
    MySQLConnectOptions connectOptions = dbHelper.startDb();
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new MySQLExample(connectOptions)).await();
  }

  public MySQLExample(SqlConnectOptions options) {
    super(options);
  }
}
