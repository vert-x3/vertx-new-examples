package io.vertx.example.web.cors;

import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.VerticleBase;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.launcher.application.VertxApplication;

import java.util.HashSet;
import java.util.Set;

/*
 * @author <a href="mailto:pmlopes@gmail.com">Paulo Lopes</a>
 * reviewed by: Giacomo Venturini mail: giacomo.venturini3@gmail.com"
 */
public class Server extends VerticleBase {

  public static void main(String[] args) {
    VertxApplication.main(new String[]{Server.class.getName()});
  }

  @Override
  public Future<?> start() throws Exception {

    Router router = Router.router(vertx);

    Set<String> allowedHeaders = new HashSet<>();
    allowedHeaders.add("x-requested-with");
    allowedHeaders.add("Access-Control-Allow-Origin");
    allowedHeaders.add("origin");
    allowedHeaders.add("Content-Type");
    allowedHeaders.add("accept");
    allowedHeaders.add("X-PINGARUNER");

    Set<HttpMethod> allowedMethods = new HashSet<>();
    allowedMethods.add(HttpMethod.GET);
    allowedMethods.add(HttpMethod.POST);
    allowedMethods.add(HttpMethod.OPTIONS);
    /*
     * these methods aren't necessary for this sample,
     * but you may need them for your projects
     */
    allowedMethods.add(HttpMethod.DELETE);
    allowedMethods.add(HttpMethod.PATCH);
    allowedMethods.add(HttpMethod.PUT);

    router.route().handler(CorsHandler.create().addRelativeOrigin(".*").allowedHeaders(allowedHeaders).allowedMethods(allowedMethods));

    router.get("/access-control-with-get").handler(ctx -> {
      HttpServerResponse httpServerResponse = ctx.response();
      httpServerResponse.setChunked(true);
      MultiMap headers = ctx.request().headers();
      for (String key : headers.names()) {
        httpServerResponse.write(key + ": ");
        httpServerResponse.write(headers.get(key));
        httpServerResponse.write("<br>");
      }
      httpServerResponse.putHeader("Content-Type", "application/text").end("Success");
    });

    router.post("/access-control-with-post-preflight").handler(ctx -> {
      HttpServerResponse httpServerResponse = ctx.response();
      httpServerResponse.setChunked(true);
      MultiMap headers = ctx.request().headers();
      for (String key : headers.names()) {
        httpServerResponse.write(key + ": ");
        httpServerResponse.write(headers.get(key));
        httpServerResponse.write("<br>");
      }
      httpServerResponse.putHeader("Content-Type", "application/text").end("Success");
    });

    // Serve the static resources
    router.route().handler(StaticHandler.create("io/vertx/example/web/cors/webroot"));

    return vertx
      .createHttpServer()
      .requestHandler(router)
      .listen(8080);
  }
}
