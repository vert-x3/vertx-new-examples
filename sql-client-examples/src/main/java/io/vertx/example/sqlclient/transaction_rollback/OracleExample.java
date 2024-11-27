package io.vertx.example.sqlclient.transaction_rollback;

import io.vertx.core.Vertx;
import io.vertx.example.sqlclient.OracleServerHelper;
import io.vertx.oracleclient.OracleConnectOptions;
import io.vertx.sqlclient.SqlConnectOptions;

public class OracleExample extends SqlClientExample {

  // Convenience method so you can run it in your IDE
  public static void main(String[] args) {
    OracleServerHelper dbHelper = new OracleServerHelper();
    OracleConnectOptions connectOptions = dbHelper.startDb();
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new OracleExample(connectOptions)).await();
  }

  public OracleExample(SqlConnectOptions options) {
    super(options);
  }
}
