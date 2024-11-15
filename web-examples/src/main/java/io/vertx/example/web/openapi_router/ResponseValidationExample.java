package io.vertx.example.web.openapi_router;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.openapi.router.OpenAPIRoute;
import io.vertx.ext.web.openapi.router.RouterBuilder;
import io.vertx.launcher.application.VertxApplication;
import io.vertx.openapi.contract.OpenAPIContract;
import io.vertx.openapi.validation.ResponseValidator;
import io.vertx.openapi.validation.ValidatableResponse;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ResponseValidationExample extends VerticleBase {

  public static void main(String[] args) {
    VertxApplication.main(new String[]{ResponseValidationExample.class.getName()});
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
    return OpenAPIContract
      .from(vertx, getContractFilePath())
      .compose(contract -> {
        // Create the ResponseValidator
        ResponseValidator responseValidator = ResponseValidator.create(vertx, contract);
        // Create the RouterBuilder
        RouterBuilder routerBuilder = RouterBuilder.create(vertx, contract);
        // Get the OpenAPIRoute for Operation showPetById
        OpenAPIRoute showPetByIdRoute = routerBuilder.getRoute("showPetById");
        // Add handler for the OpenAPIRoute
        showPetByIdRoute.addHandler(routingContext -> {
          // Create the payload
          JsonObject pet = buildPet(1337, "Foo");
          // Build the Response
          ValidatableResponse validatableResponse = ValidatableResponse.create(200, pet.toBuffer(), "application/json");
          // Validate the response
          responseValidator.validate(validatableResponse, showPetByIdRoute.getOperation().getOperationId())
            .onFailure(routingContext::fail)
            // send back the validated response
            .onSuccess(validatedResponse -> validatedResponse.send(routingContext.response()));
        });

        // Create the Router
        Router basePathRouter = Router.router(vertx);
        // Create the OpenAPi Router and mount it on the base path (must match the contract)
        basePathRouter.route("/v1/*").subRouter(routerBuilder.createRouter());

        return vertx.createHttpServer().requestHandler(basePathRouter).listen(0, "localhost");
      }).onSuccess(server -> {
        System.out.println("Server started on port " + server.actualPort());

        WebClient.create(vertx)
          .get(server.actualPort(), "localhost", "/v1/pets/1337")
          // send request with a payload that does fit to the contract
          .send().onSuccess(response -> {
            System.out.println("Response status code expected 200:  " + response.statusCode());
            System.exit(0);
          }).onFailure(t -> {
            t.printStackTrace();
            System.exit(1);
          });
      });
  }
}
