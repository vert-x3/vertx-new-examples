package io.vertx.example.web.openapi_router;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.openapi.router.RouterBuilder;
import io.vertx.launcher.application.VertxApplication;
import io.vertx.openapi.contract.OpenAPIContract;
import io.vertx.openapi.validation.ValidatedRequest;

import java.nio.file.Path;
import java.nio.file.Paths;

import static io.vertx.ext.web.openapi.router.RouterBuilder.KEY_META_DATA_VALIDATED_REQUEST;

public class RequestValidationExample extends VerticleBase {

  public static void main(String[] args) {
    VertxApplication.main(new String[]{ResponseValidationExample.class.getName()});
  }

  private String getContractFilePath() {
    Path resourceDir = Paths.get("src", "test", "resources");
    Path packagePath = Paths.get(this.getClass().getPackage().getName().replace(".", "/"));
    return resourceDir.resolve(packagePath).resolve("petstore.json").toString();
  }

  @Override
  public Future<?> start() {
    return OpenAPIContract
      .from(vertx, getContractFilePath())
      .compose(contract -> {
        // Create the RouterBuilder
        RouterBuilder routerBuilder = RouterBuilder.create(vertx, contract);
        // Add handler for Operation showPetById
        routerBuilder.getRoute("showPetById").addHandler(routingContext -> {
          // Get the validated request
          ValidatedRequest validatedRequest = routingContext.get(KEY_META_DATA_VALIDATED_REQUEST);
          // Get the parameter value
          int petId = validatedRequest.getPathParameters().get("petId").getInteger();

          // Due to the fact that we don't validate the resonse here, you can send back a response,
          // that doesn't fit to the contract
          routingContext.response().setStatusCode(200).end();
        });

        // Create the Router
        Router router = routerBuilder.createRouter();
        return vertx.createHttpServer().requestHandler(router).listen(0, "localhost");
      })
      .onSuccess(server -> System.out.println("Server started on port " + server.actualPort()));
  }
}
