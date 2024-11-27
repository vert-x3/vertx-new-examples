package io.vertx.example.sqlclient.query_params;

import io.vertx.core.Vertx;
import io.vertx.example.sqlclient.SqlServerHelper;
import io.vertx.mssqlclient.MSSQLConnectOptions;
import io.vertx.sqlclient.SqlConnectOptions;

public class SqlServerExample extends SqlClientExample {

  // Convenience method so you can run it in your IDE
  public static void main(String[] args) {
    SqlServerHelper dbHelper = new SqlServerHelper();
    MSSQLConnectOptions connectOptions = dbHelper.startDb();
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new SqlServerExample(connectOptions)).await();
  }

  public SqlServerExample(SqlConnectOptions options) {
    super(options);
  }

  @Override
  protected String selectQuery() {
    return "select * from test where id = @p1";
  }
}
