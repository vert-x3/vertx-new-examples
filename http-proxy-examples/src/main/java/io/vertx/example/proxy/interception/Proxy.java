package io.vertx.example.proxy.interception;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpServer;
import io.vertx.httpproxy.HttpProxy;
import io.vertx.httpproxy.interceptors.BodyInterceptor;
import io.vertx.httpproxy.interceptors.BodyTransformer;
import io.vertx.httpproxy.interceptors.HeadInterceptor;
import io.vertx.launcher.application.VertxApplication;

import java.util.Set;

public class Proxy extends VerticleBase {

  public static void main(String[] args) {
    VertxApplication.main(new String[]{Proxy.class.getName()});
  }

  @Override
  public Future<?> start() {
    HttpClient proxyClient = vertx.createHttpClient();

    HttpProxy proxy = HttpProxy.reverseProxy(proxyClient);
    proxy.origin(7070, "localhost");

    HeadInterceptor headInterceptor = HeadInterceptor.builder()
      .addingPathPrefix("/app")
      .filteringResponseHeaders(Set.of("x-internal-header"))
      .build();
    proxy.addInterceptor(headInterceptor);

    BodyTransformer responseTransformer = BodyTransformer.transformText(txt -> {
      return txt.replace("Hello", "Hi");
    }, "ISO-8859-1");
    proxy.addInterceptor(BodyInterceptor.modifyResponseBody(responseTransformer));

    HttpServer proxyServer = vertx.createHttpServer();

    return proxyServer.requestHandler(proxy).listen(8080);
  }
}
