package io.vertx.example.otel;

public class OpenTelemetryExample {

  public static void main(String[] args) throws Exception {
    JokeService.main(args);
    HelloService.main(args);
    Gateway.main(args);
  }
}
