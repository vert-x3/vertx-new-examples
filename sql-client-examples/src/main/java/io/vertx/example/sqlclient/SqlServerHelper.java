package io.vertx.example.sqlclient;

import io.vertx.mssqlclient.MSSQLConnectOptions;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

public class SqlServerHelper {

  private final GenericContainer<?> mssql;

  public SqlServerHelper() {
    mssql = new GenericContainer<>("mcr.microsoft.com/mssql/server:2019-latest")
      .withEnv("ACCEPT_EULA", "Y")
      .withEnv("SA_PASSWORD", "A_Str0ng_Required_Password")
      .withExposedPorts(1433)
      .waitingFor(Wait.forLogMessage(".*The tempdb database has \\d+ data file\\(s\\).*\\n", 2));
  }

  public MSSQLConnectOptions startDb() {
    mssql.start();
    return new MSSQLConnectOptions()
      .setPort(mssql.getMappedPort(1433))
      .setHost(mssql.getHost())
      .setUser("SA")
      .setPassword("A_Str0ng_Required_Password");
  }
}
