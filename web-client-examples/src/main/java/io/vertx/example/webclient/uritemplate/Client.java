package io.vertx.example.webclient.uritemplate;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.ext.web.client.WebClient;
import io.vertx.launcher.application.VertxApplication;
import io.vertx.uritemplate.UriTemplate;

/*
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class Client extends VerticleBase {

  public static void main(String[] args) {
    VertxApplication.main(new String[]{Client.class.getName()});
  }

  private static final UriTemplate REQUEST_URI = UriTemplate.of("/{?firstName}{&lastName}{&male}");

  private WebClient client;

  @Override
  public Future<?> start() throws Exception {

    client = WebClient.create(vertx);

    return client.get(8080, "localhost", REQUEST_URI)
      .setTemplateParam("firstName", "Dale")
      .setTemplateParam("lastName", "Cooper")
      .setTemplateParam("male", "true")
      .send()
      .onSuccess(response -> System.out.println("Got HTTP response with status " + response.statusCode()));
  }
}
