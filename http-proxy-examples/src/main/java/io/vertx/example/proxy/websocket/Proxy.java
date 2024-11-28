package io.vertx.example.proxy.websocket;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpServer;
import io.vertx.httpproxy.HttpProxy;
import io.vertx.launcher.application.VertxApplication;

public class Proxy extends VerticleBase {

  public static void main(String[] args) {
    VertxApplication.main(new String[]{Proxy.class.getName()});
  }

  @Override
  public Future<?> start() {
    HttpClient proxyClient = vertx.createHttpClient();

    HttpProxy proxy = HttpProxy.reverseProxy(proxyClient);
    proxy.origin(7070, "localhost");

    HttpServer proxyServer = vertx.createHttpServer();

    return proxyServer.requestHandler(proxy).listen(8080);
  }
}
