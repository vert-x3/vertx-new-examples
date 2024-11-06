package io.vertx.examples.mongo;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.launcher.application.VertxApplication;
import org.testcontainers.containers.MongoDBContainer;

public class MongoClientVerticle extends VerticleBase {

  private static final MongoDBContainer MONGO_DB_CONTAINER = new MongoDBContainer("mongo:4.0.10");

  public static void main(String[] args) {
    MONGO_DB_CONTAINER.start();

    VertxApplication.main(new String[]{MongoClientVerticle.class.getName()});
  }

  private MongoClient mongoClient;

  @Override
  public Future<?> start() {

    JsonObject mongoconfig = new JsonObject()
      .put("connection_string", MONGO_DB_CONTAINER.getConnectionString())
      .put("db_name", "test");

    mongoClient = MongoClient.createShared(vertx, mongoconfig);

    JsonObject product1 = new JsonObject().put("itemId", "12345").put("name", "Cooler").put("price", "100.0");

    return mongoClient.save("products", product1)
      .compose(id -> {
        System.out.println("Inserted id: " + id);
        return mongoClient.find("products", new JsonObject().put("itemId", "12345"));
      })
      .compose(res -> {
        System.out.println("Name is " + res.get(0).getString("name"));
        return mongoClient.removeDocument("products", new JsonObject().put("itemId", "12345"));
      })
      .onSuccess(res -> {
        System.out.println("Product removed ");
      });
  }
}
