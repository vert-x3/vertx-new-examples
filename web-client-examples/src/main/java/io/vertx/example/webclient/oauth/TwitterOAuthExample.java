package io.vertx.example.webclient.oauth;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.http.HttpResponseExpectation;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.launcher.application.VertxApplication;

/**
 * todo: rewrite this without Twitter
 *
 * @author <a href="mailto:akshay0007k@gmail.com">Akshay Kumar</a>
 */
public class TwitterOAuthExample extends VerticleBase {

  // consumer key and secret are provided by twitter after registering your app.
  private static final String B64_ENCODED_AUTH = "base64(your-consumer-key:your-consumer-secret)";
  private static final String AUTH_URL = "https://api.twitter.com/oauth2/token";
  private static final String TWEET_SEARCH_URL = "https://api.twitter.com/1.1/search/tweets.json";

  public static void main(String[] args) {
    VertxApplication.main(new String[]{TwitterOAuthExample.class.getName()});
  }

  private WebClient client;

  @Override
  public Future<?> start() throws Exception {

    // Create the web client.
    client = WebClient.create(vertx);

    String queryToSearch = "vertx";

    // First we need to authenticate our call.
    String authHeader = "Basic " + B64_ENCODED_AUTH;

    return client.postAbs(AUTH_URL)
      .as(BodyCodec.jsonObject())
      .addQueryParam("grant_type", "client_credentials")
      .putHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
      .putHeader("Authorization", authHeader)
      .send()
      .expecting(HttpResponseExpectation.SC_OK)
      .compose(authResult -> {
        // Authentication successful.
        JsonObject authJson = authResult.body();
        String accessToken = authJson.getString("access_token");
        String header = "Bearer " + accessToken;
        // Making call to search tweets.
        return client.getAbs(TWEET_SEARCH_URL)
          .as(BodyCodec.jsonObject())
          .addQueryParam("q", queryToSearch)
          .putHeader("Authorization", header)
          .send()
          .expecting(HttpResponseExpectation.SC_OK)
          .map(HttpResponse::body);
      })
      .onSuccess(success -> {
        System.out.println(success);
      });
  }
}
