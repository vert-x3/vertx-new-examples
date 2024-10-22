package io.vertx.example.web.validation;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.validation.BadRequestException;
import io.vertx.ext.web.validation.RequestParameters;
import io.vertx.ext.web.validation.ValidationHandler;
import io.vertx.ext.web.validation.builder.ValidationHandlerBuilder;
import io.vertx.json.schema.Draft;
import io.vertx.json.schema.JsonSchemaOptions;
import io.vertx.json.schema.SchemaRepository;
import io.vertx.launcher.application.VertxApplication;

import static io.vertx.ext.web.validation.builder.Bodies.formUrlEncoded;
import static io.vertx.ext.web.validation.builder.Bodies.json;
import static io.vertx.ext.web.validation.builder.Parameters.param;
import static io.vertx.json.schema.common.dsl.Schemas.*;

/**
 * @author <a href="https://slinkydeveloper.com">Francesco Guardiani</a>
 */
public class ValidationExampleServer extends VerticleBase {

  @Override
  public Future<?> start() throws Exception {
    // The schema parser is required to create new schemas
    SchemaRepository repo = SchemaRepository.create(new JsonSchemaOptions().setDraft(Draft.DRAFT7));

    Router router = Router.router(vertx);

    // If you want to validate bodies, don't miss that handler!
    router.route().handler(BodyHandler.create());

    // Create Validation Handler with some stuff
    ValidationHandler validationHandler = ValidationHandlerBuilder.create(repo)
      .queryParameter(param("parameterName", intSchema()))
      .pathParameter(param("pathParam", numberSchema()))
      .body(
        formUrlEncoded(
          objectSchema()
            .property("r", intSchema())
            .property("g", intSchema())
            .property("b", intSchema())
        )
      )
      .build();

    router.post("/hello/:pathParam")
      // Mount validation handler
      .handler(validationHandler)
      //Mount your handler
      .handler((routingContext) -> {
        // To consume parameters you can get it in the "classic way" of Vert.x Web
        // or you can use the RequestParameters that contains parameters already parsed (and maybe transformed)
        // Get RequestParameters container
        RequestParameters params = routingContext.get(ValidationHandler.REQUEST_CONTEXT_KEY);

        // Get parameters
        Integer parameterName = params.queryParameter("parameterName").getInteger();
        Float pathParam = params.pathParameter("pathParam").getFloat();

        // Get body
        JsonObject rgbObject = params.body().getJsonObject();

        // Do awesome things with your parameters!
      });

    // A very basic example of JSON body validation
    router.post("/jsonUploader")
      .handler(ValidationHandlerBuilder.create(repo)
        .body(json(
          objectSchema()
            .additionalProperties(stringSchema())
        ))
        .build()
      )
      .handler((routingContext -> {
        RequestParameters params = routingContext.get("parsedParameters");
        JsonObject body = params.body().getJsonObject();
      }));

    // Custom handler for the ValidationException
    router.errorHandler(400, routingContext -> {
      Throwable failure = routingContext.failure();
      if (failure instanceof BadRequestException) {
        // Something went wrong during validation!
        String validationErrorMessage = failure.getMessage();
        routingContext.response().setStatusCode(400).end();
      }
    });

    return vertx
      .createHttpServer()
      .requestHandler(router)
      .listen();
  }

  public static void main(String[] args) {
    VertxApplication.main(new String[]{ValidationExampleServer.class.getName()});
  }
}
