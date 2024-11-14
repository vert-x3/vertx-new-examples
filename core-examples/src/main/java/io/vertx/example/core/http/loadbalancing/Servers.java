package io.vertx.example.core.http.loadbalancing;

import io.vertx.core.Vertx;
import io.vertx.example.core.http.simple.Server;

public class Servers {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new Server(8080)).await();
    vertx.deployVerticle(new Server(8081)).await();
    vertx.deployVerticle(new Server(8082)).await();
    System.out.println("Servers listening on ports 8080/8081/8082");
  }
}
