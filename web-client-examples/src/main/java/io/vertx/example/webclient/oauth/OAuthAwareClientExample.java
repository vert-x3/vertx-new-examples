package io.vertx.example.webclient.oauth;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.auth.oauth2.OAuth2FlowType;
import io.vertx.ext.auth.oauth2.OAuth2Options;
import io.vertx.ext.auth.oauth2.Oauth2Credentials;
import io.vertx.ext.auth.oauth2.providers.KeycloakAuth;
import io.vertx.ext.web.client.OAuth2WebClient;
import io.vertx.ext.web.client.OAuth2WebClientOptions;
import io.vertx.ext.web.client.WebClient;
import io.vertx.launcher.application.VertxApplication;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

/**
 * @author <a href="mailto:lazarbulic@gmail.com">Lazar Bulic</a>
 */
public class OAuthAwareClientExample extends VerticleBase {

  private static final String KEYCLOAK_IMAGE = "quay.io/keycloak/keycloak:26.0.2";

  private static GenericContainer<?> keycloakContainerCustom;

  public static void main(String[] args) {
    keycloakContainerCustom = new GenericContainer<>(DockerImageName.parse(KEYCLOAK_IMAGE))
      .withExposedPorts(8080)
      .withCopyFileToContainer(MountableFile.forClasspathResource("/realm-export.json", 0644), "/opt/keycloak/data/import/realm-export.json")
      .withCommand("start-dev", "--import-realm");

    keycloakContainerCustom
      .setWaitStrategy(Wait.forLogMessage(".*Running the server in development mode\\. DO NOT use this configuration in production.*\\n", 1));
    keycloakContainerCustom.start();

    VertxApplication.main(new String[]{OAuthAwareClientExample.class.getName()});
  }

  private OAuth2WebClient oAuth2WebClient;

  @Override
  public Future<?> start() throws Exception {
    mockServer();

    // Create base web client
    WebClient client = WebClient.create(vertx);

    // Discover Keycloak
    return KeycloakAuth.discover(
        vertx,
        new OAuth2Options()
          .setClientId("vertx-examples-client")
          .setClientSecret("Bccl2MPUjEiLYaMSeTeZ30OesPxY4c1k")
          .setSite("http://localhost:" + keycloakContainerCustom.getMappedPort(8080) + "/realms/{realm}")
          .setTenant("vertx-examples"))
      .onSuccess(keycloakAuth -> {

        // Create OAuth2WebClient with Keycloak OAuth2 configuration
        oAuth2WebClient = OAuth2WebClient.create(client, keycloakAuth,
          new OAuth2WebClientOptions()
            .setLeeway(5) //If a request is to be performed the current active user object is checked for expiration with the extra given leeway
            .setRenewTokenOnForbidden(true)); // client will perform a new token request retry the original request before passing the response to the user handler/promise
      })
      .flatMap(ignore -> {

        // Send request to protected resource with client_credentials flow
        return oAuth2WebClient
          .withCredentials(new Oauth2Credentials().setFlow(OAuth2FlowType.CLIENT))
          .get(8081, "localhost", "protected/path")
          .send()
          .onSuccess(response -> {
            System.out.println("Got HTTP response with status " + response.statusCode() + " from " + response.bodyAsString());
          });
      })
      .flatMap(ignore -> {

        // Send request to protected resource with password flow
        return oAuth2WebClient
          .withCredentials(new Oauth2Credentials().setFlow(OAuth2FlowType.PASSWORD)
            .setUsername("janedoe")
            .setPassword("s3cr3t"))
          .get(8081, "localhost", "protected/path")
          .send()
          .onSuccess(response -> {
            System.out.println("Got HTTP response with status " + response.statusCode() + " from " + response.bodyAsString());
          });
      });
  }

  public void mockServer() {
    vertx.createHttpServer().requestHandler(req -> {
      if (req.method() == HttpMethod.GET && "protected/path".equals(req.path())) {
        String token = req.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
          req.response().setStatusCode(401).end();
        } else {
          req.response().end("Protected resource");
        }
      } else {
        req.response().end("Public resource");
      }
    }).listen(8081);
  }
}
