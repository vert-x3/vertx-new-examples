package io.vertx.example.sqlclient;

import io.vertx.oracleclient.OracleConnectOptions;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

public class OracleServerHelper {

  private final GenericContainer<?> oracle;

  public OracleServerHelper() {
    oracle = new GenericContainer<>("gvenzl/oracle-free:23-slim-faststart")
      .withEnv("ORACLE_PASSWORD", "vertx")
      .withExposedPorts(1521)
      .waitingFor(Wait.forLogMessage(".*DATABASE IS READY TO USE!.*\\n", 1));
  }

  public OracleConnectOptions startDb() {
    oracle.start();
    return new OracleConnectOptions()
      .setPort(oracle.getMappedPort(1521))
      .setHost(oracle.getHost())
      .setUser("sys as sysdba")
      .setPassword("vertx")
      .setDatabase("FREEPDB1");

  }
}
