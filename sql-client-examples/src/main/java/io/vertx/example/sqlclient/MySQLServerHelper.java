package io.vertx.example.sqlclient;

import io.vertx.mysqlclient.MySQLConnectOptions;
import org.testcontainers.containers.GenericContainer;

public class MySQLServerHelper {

  private final GenericContainer<?> mysql;

  public MySQLServerHelper() {
    mysql = new GenericContainer<>("mysql:9")
      .withEnv("MYSQL_RANDOM_ROOT_PASSWORD", "yes")
      .withEnv("MYSQL_USER", "mysql")
      .withEnv("MYSQL_PASSWORD", "mysql")
      .withEnv("MYSQL_DATABASE", "mysql")
      .withExposedPorts(3306);
  }

  public MySQLConnectOptions startDb() {
    mysql.start();
    return new MySQLConnectOptions()
      .setPort(mysql.getMappedPort(3306))
      .setHost(mysql.getHost())
      .setDatabase("mysql")
      .setUser("mysql")
      .setPassword("mysql");
  }
}
