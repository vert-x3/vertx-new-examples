package io.vertx.example.web.openapi_router;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
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
    Path resourceDir = Paths.get("src", "test", "resources");
    Path packagePath = Paths.get(this.getClass().getPackage().getName().replace(".", "/"));
    return resourceDir.resolve(packagePath).resolve("petstore.json").toString();
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
        Router router = routerBuilder.createRouter();
        return vertx.createHttpServer().requestHandler(router).listen(0, "localhost");
      })
      .onSuccess(server -> System.out.println("Server started on port " + server.actualPort()));
  }
}
