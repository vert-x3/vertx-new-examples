package io.vertx.example.web.openapi3;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.openapi.RouterBuilder;
import io.vertx.ext.web.openapi.RouterBuilderOptions;
import io.vertx.ext.web.validation.RequestParameter;
import io.vertx.ext.web.validation.RequestParameters;
import io.vertx.ext.web.validation.ValidationHandler;
import io.vertx.launcher.application.VertxApplication;

public class OpenAPI3Server extends VerticleBase {

  public static void main(String[] args) {
    VertxApplication.main(new String[]{OpenAPI3Server.class.getName()});
  }

  private HttpServer server;

  @Override
  public Future<?> start() {
    // Load the api spec. This operation is asynchronous
    return RouterBuilder
      .create(this.vertx, "petstore.yaml")
      .compose(routerBuilder -> {
        // Before router creation you can enable/disable various router factory behaviours
        RouterBuilderOptions factoryOptions = new RouterBuilderOptions()
          .setMountResponseContentTypeHandler(true); // Mount ResponseContentTypeHandler automatically
        routerBuilder.setOptions(factoryOptions);

        // Setup an handler for listPets
        routerBuilder.operation("listPets")
          .handler(routingContext -> {
            // Load the parsed parameters
            RequestParameters params = routingContext.get(ValidationHandler.REQUEST_CONTEXT_KEY);
            // Handle listPets operation
            RequestParameter limitParameter = params.queryParameter(/* Parameter name */ "limit");
            if (limitParameter != null) {
              // limit parameter exists, use it!
              Integer limit = limitParameter.getInteger();
            } else {
              // limit parameter doesn't exist (it's not required).
              // If it's required you don't have to check if it's null!
            }
            routingContext.response().setStatusCode(200).end();
          });

        // Create the router
        Router router = routerBuilder.createRouter();

        // Now you can use your Router instance
        server = vertx
          .createHttpServer(new HttpServerOptions().setPort(8080).setHost("localhost"))
          .requestHandler(router);
        return server.listen();
      });
  }
}
