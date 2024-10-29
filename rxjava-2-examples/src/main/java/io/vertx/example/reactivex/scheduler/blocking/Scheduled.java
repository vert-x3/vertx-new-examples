package io.vertx.example.reactivex.scheduler.blocking;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import io.vertx.launcher.application.VertxApplication;
import io.vertx.reactivex.core.AbstractVerticle;

/*
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class Scheduled extends AbstractVerticle {

  public static void main(String[] args) {
    VertxApplication.main(new String[]{Scheduled.class.getName()});
  }

  private String blockingLoad(String id) {

    // Simulate a blocking action
    try {
      Thread.sleep(100);
      return "someData for " + id + " on thread " + Thread.currentThread().getName();
    } catch (InterruptedException e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public Completable rxStart() {

    Flowable<String> o = Flowable.just("someID1", "someID2", "someID3", "someID4");

    // This scheduler can execute blocking actions
    Scheduler scheduler = io.vertx.reactivex.core.RxHelper.blockingScheduler(vertx);

    // All operations done on the observer now can be blocking
    o = o.observeOn(scheduler);

    // Load from a blocking api
    o = o.map(this::blockingLoad
    );

    return o.doOnNext(item -> {
      System.out.println("Got item " + item);
    }).ignoreElements();
  }
}
