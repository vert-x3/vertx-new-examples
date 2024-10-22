package io.vertx.example.core.future;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.VerticleBase;
import io.vertx.launcher.application.VertxApplication;

import java.util.function.Function;

/**
 * An example showing how to use {@link io.vertx.core.Future#compose(Function)}
 */
public class ComposeExample extends VerticleBase {

  public static void main(String[] args) {
    VertxApplication.main(new String[]{ComposeExample.class.getName()});
  }


  @Override
  public Future<?> start() throws Exception {
    Future<String> future = anAsyncAction();
    return future.compose(this::anotherAsyncAction)
      .onComplete(ar -> {
        if (ar.failed()) {
          System.out.println("Something bad happened");
          ar.cause().printStackTrace();
        } else {
          System.out.println("Result: " + ar.result());
        }
      });
  }

  private Future<String> anAsyncAction() {
    Promise<String> promise = Promise.promise();
    // mimic something that take times
    vertx.setTimer(100, l -> promise.complete("world"));
    return promise.future();
  }

  private Future<String> anotherAsyncAction(String name) {
    Promise<String> promise = Promise.promise();
    // mimic something that take times
    vertx.setTimer(100, l -> promise.complete("hello " + name));
    return promise.future();
  }
}
