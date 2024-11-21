package io.vertx.example.config;

import io.vertx.config.ConfigRetriever;
import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.json.JsonObject;
import io.vertx.launcher.application.VertxApplication;

public class Server extends VerticleBase {

  public static void main(String[] args) {
    //System.setProperty("message", "Hi Vert.x Config! (sysprop)");
    //System.setProperty("port", "8888");
    JsonObject verticleConfig = new JsonObject()
      .put("message", "Hi Vert.x Config! (verticle config)")
      .put("port", 8080);
    VertxApplication.main(new String[]{Server.class.getName(), "-conf", verticleConfig.encode()});
  }

  @Override
  public Future<?> start() {
    ConfigRetriever retriever = ConfigRetriever.create(vertx);
    return retriever.getConfig().compose(cfg -> vertx.createHttpServer()
      .requestHandler(req -> req.response().end(cfg.getString("message", "Hello World!")))
      .listen(cfg.getInteger("port", 8080))
    );
  }
}
