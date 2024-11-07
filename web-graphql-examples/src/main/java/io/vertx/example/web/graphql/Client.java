package io.vertx.example.web.graphql;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.launcher.application.VertxApplication;

import static io.vertx.core.http.HttpResponseExpectation.JSON;
import static io.vertx.core.http.HttpResponseExpectation.SC_OK;

public class Client extends VerticleBase {

  public static void main(String[] args) {
    VertxApplication.main(new String[]{Client.class.getName()});
  }

  private WebClient webClient;

  @Override
  public Future<?> start() {

    webClient = WebClient.create(vertx, new WebClientOptions().setDefaultPort(8080));

    JsonObject request = new JsonObject()
      .put("query", "query($secure: Boolean) { allLinks(secureOnly: $secure) { url, postedBy { name } } }")
      .put("variables", new JsonObject().put("secure", true));

    return webClient
      .post("/graphql")
      .as(BodyCodec.jsonObject())
      .sendJsonObject(request)
      .expecting(SC_OK.and(JSON))
      .onSuccess(response -> {
        System.out.println("response = " + response.body().encodePrettily());
      });
  }
}
