package io.vertx.example.proxy.simple;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpServer;
import io.vertx.httpproxy.HttpProxy;
import io.vertx.httpproxy.ProxyOptions;
import io.vertx.launcher.application.VertxApplication;

public class Proxy extends VerticleBase {

  public static void main(String[] args) {
    VertxApplication.main(new String[]{Proxy.class.getName()});
  }

  @Override
  public Future<?> start() {
    HttpClient proxyClient = vertx.createHttpClient();

    HttpProxy proxy = HttpProxy.reverseProxy(new ProxyOptions().setSupportWebSocket(true), proxyClient);
    proxy.origin(7070, "localhost");

    HttpServer proxyServer = vertx.createHttpServer();

    return proxyServer.requestHandler(proxy).listen(8080);
  }
}
