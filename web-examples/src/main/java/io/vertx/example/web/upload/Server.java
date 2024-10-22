package io.vertx.example.web.upload;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.ext.web.FileUpload;
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
    router.route().handler(BodyHandler.create().setUploadsDirectory("uploads"));

    router.route("/").handler(routingContext -> {
      routingContext.response().putHeader("content-type", "text/html").end(
        "<form action=\"/form\" method=\"post\" enctype=\"multipart/form-data\">\n" +
          "    <div>\n" +
          "        <label for=\"name\">Select a file:</label>\n" +
          "        <input type=\"file\" name=\"file\" />\n" +
          "    </div>\n" +
          "    <div class=\"button\">\n" +
          "        <button type=\"submit\">Send</button>\n" +
          "    </div>" +
          "</form>"
      );
    });

    // handle the form
    router.post("/form").handler(ctx -> {
      ctx.response().putHeader("Content-Type", "text/plain");

      ctx.response().setChunked(true);

      for (FileUpload f : ctx.fileUploads()) {
        System.out.println("f");
        ctx.response().write("Filename: " + f.fileName());
        ctx.response().write("\n");
        ctx.response().write("Size: " + f.size());
      }

      ctx.response().end();
    });

    return vertx
      .createHttpServer()
      .requestHandler(router)
      .listen(8080);
  }
}
