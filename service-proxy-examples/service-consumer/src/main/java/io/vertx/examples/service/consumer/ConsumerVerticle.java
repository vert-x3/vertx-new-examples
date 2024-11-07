package io.vertx.examples.service.consumer;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.json.JsonObject;
import io.vertx.examples.service.ProcessorService;
import io.vertx.launcher.application.VertxApplication;
import io.vertx.serviceproxy.ServiceException;

import static io.vertx.examples.service.ProcessorService.BAD_NAME_ERROR;
import static io.vertx.examples.service.ProcessorService.NO_NAME_ERROR;


/**
 * A verticle consuming the provided {@link ProcessorService} service.
 */
public class ConsumerVerticle extends VerticleBase {

  public static void main(String[] args) {
    VertxApplication.main(new String[]{ConsumerVerticle.class.getName(), "-cluster"});
  }

  @Override
  public Future<?> start() {
    ProcessorService service = ProcessorService.createProxy(vertx, "vertx.processor");

    JsonObject document = new JsonObject().put("name", "vertx");

    return service
      .process(document)
      .recover(err -> dealWithFailure(err))
      .onSuccess(json -> System.out.println(json.encodePrettily()));
  }

  public static Future<JsonObject> dealWithFailure(Throwable t) {
    if (t instanceof ServiceException) {
      ServiceException exc = (ServiceException) t;
      if (exc.failureCode() == BAD_NAME_ERROR) {
        return Future.failedFuture("Failed to process the document: The name in the document is bad. " +
          "The name provided is: " + exc.getDebugInfo().getString("name"));
      } else if (exc.failureCode() == NO_NAME_ERROR) {
        return Future.failedFuture("Failed to process the document: No name was found");
      }
    }
    return Future.failedFuture("Unexpected error: " + t);
  }
}
