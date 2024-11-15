package io.vertx.example.web.openapi_router;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.openapi.router.RouterBuilder;
import io.vertx.launcher.application.VertxApplication;
import io.vertx.openapi.contract.OpenAPIContract;
import io.vertx.openapi.validation.ValidatedRequest;

import java.nio.file.Path;
import java.nio.file.Paths;

import static io.vertx.ext.web.openapi.router.RouterBuilder.KEY_META_DATA_VALIDATED_REQUEST;

public class RequestValidationExample extends VerticleBase {

  public static void main(String[] args) {
    VertxApplication.main(new String[]{RequestValidationExample.class.getName()});
  }

  private String getContractFilePath() {
    Path resourceDir = Paths.get("src", "main", "resources");
    Path packagePath = Paths.get(this.getClass().getPackage().getName().replace(".", "/"));
    Path filePath = resourceDir.resolve(packagePath).resolve("petstore.yaml");
    if (filePath.toAbsolutePath().toString().contains("web-examples")) {
      return filePath.toString();
    } else {
      return Path.of("web-examples").resolve(filePath).toString();
    }
  }


  private JsonObject buildPet(int id, String name) {
    return new JsonObject().put("id", id).put("name", name);
  }


  @Override
  public Future<?> start() {
    JsonObject expectedPet = buildPet(1337, "Foo");

    Future<HttpServer> serverStarted = OpenAPIContract
      .from(vertx, getContractFilePath())
      .compose(contract -> {
        // Create the RouterBuilder
        RouterBuilder routerBuilder = RouterBuilder.create(vertx, contract);
        // Add handler for Operation showPetById
        routerBuilder.getRoute("createPets").addHandler(routingContext -> {
          // Get the validated request
          ValidatedRequest validatedRequest = routingContext.get(KEY_META_DATA_VALIDATED_REQUEST);
          // Get the parameter value
          JsonObject newPet = validatedRequest.getBody().getJsonObject();
          if (newPet.equals(expectedPet)) {
            System.out.println("Request is valid");
          }

          routingContext.response().setStatusCode(201).end();
        });

        Router basePathRouter = Router.router(vertx);
        // Create the OpenAPi Router and mount it on the base path (must match the contract)
        basePathRouter.route("/v1/*").subRouter(routerBuilder.createRouter());

        return vertx.createHttpServer().requestHandler(basePathRouter).listen(0, "localhost");
      }).onSuccess(server -> {
        System.out.println("Server started on port " + server.actualPort());
      }).onFailure(t -> {
        t.printStackTrace();
        System.exit(1);
      });

    /**
     * Send a request that does fit to the contract
     */
    serverStarted.onSuccess(server -> {
      WebClient.create(vertx)
        .post(server.actualPort(), "localhost", "/v1/pets")
        // send post request with a payload that does fit to the contract
        .sendJson(expectedPet)
        .onSuccess(response -> {
          System.out.println("Response status code expected 201:  " + response.statusCode());
        }).onFailure(t -> {
          t.printStackTrace();
          System.exit(1);
        });
    });

    /**
     * Send a request that does not fit to the contract
     */
    serverStarted.onSuccess(server -> {
      WebClient.create(vertx)
        .post(server.actualPort(), "localhost", "/v1/pets")
        // send post request with a payload that does not fit to the contract
        .sendJson(new JsonObject())
        .onSuccess(response -> {
          System.out.println("Response status code expected 400:  " + response.statusCode());
        }).onFailure(t -> {
          t.printStackTrace();
          System.exit(1);
        });
    });

    return serverStarted;
  }
}
