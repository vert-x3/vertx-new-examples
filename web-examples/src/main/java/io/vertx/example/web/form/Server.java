package io.vertx.example.web.form;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.launcher.application.VertxApplication;

/*
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class Server extends VerticleBase {

  public static void main(String[] args) {
    VertxApplication.main(new String[]{Server.class.getName()});
  }

  @Override
  public Future<?> start() throws Exception {

    Router router = Router.router(vertx);

    // Enable multipart form data parsing
    router.route().handler(BodyHandler.create());

    router.route("/").handler(routingContext -> {
      routingContext.response().putHeader("content-type", "text/html").end(
          "<form action=\"/form\" method=\"post\">\n" +
          "    <div>\n" +
          "        <label for=\"name\">Enter your name:</label>\n" +
          "        <input type=\"text\" id=\"name\" name=\"name\" />\n" +
          "    </div>\n" +
          "    <div class=\"button\">\n" +
          "        <button type=\"submit\">Send</button>\n" +
          "    </div>" +
          "</form>"
      );
    });

    // handle the form
    router.post("/form").handler(ctx -> {
      ctx.response().putHeader(HttpHeaders.CONTENT_TYPE, "text/plain");
      // note the form attribute matches the html form element name.
      ctx.response().end("Hello " + ctx.request().getParam("name") + "!");
    });

    return vertx
      .createHttpServer()
      .requestHandler(router)
      .listen(8080);
  }
}
