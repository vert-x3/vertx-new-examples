package io.vertx.example.core.eventbus.messagecodec;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.eventbus.EventBus;
import io.vertx.example.core.eventbus.messagecodec.util.CustomMessage;
import io.vertx.example.core.eventbus.messagecodec.util.CustomMessageCodec;
import io.vertx.launcher.application.VertxApplication;

/**
 * Publisher
 *
 * @author Junbong
 */
public class Sender extends VerticleBase {
  public static void main(String[] args) {
    VertxApplication.main(new String[]{Sender.class.getName(), "-cluster"});
  }

  @Override
  public Future<?> start() throws Exception {
    EventBus eventBus = vertx.eventBus();

    // Register codec for custom message
    eventBus.registerDefaultCodec(CustomMessage.class, new CustomMessageCodec());

    // Custom message
    CustomMessage clusterWideMessage = new CustomMessage(200, "a00000001", "Message sent from publisher!");
    CustomMessage localMessage = new CustomMessage(200, "a0000001", "Local message!");

    // Send a message to [cluster receiver] every second
    vertx.setPeriodic(1000, _id -> {
      eventBus.request("cluster-message-receiver", clusterWideMessage).onComplete(reply -> {
        if (reply.succeeded()) {
          CustomMessage replyMessage = (CustomMessage) reply.result().body();
          System.out.println("Received reply: " + replyMessage.getSummary());
        } else {
          System.out.println("No reply from cluster receiver");
        }
      });
    });


    // Deploy local receiver
    return vertx.deployVerticle(LocalReceiver.class.getName()).andThen(deployResult -> {
      if (deployResult.succeeded()) {
        // Send a message to [local receiver] every 2 second
        vertx.setPeriodic(2000, _id -> {
          eventBus.request("local-message-receiver", localMessage).onComplete(reply -> {
            if (reply.succeeded()) {
              CustomMessage replyMessage = (CustomMessage) reply.result().body();
              System.out.println("Received local reply: " + replyMessage.getSummary());
            } else {
              System.out.println("No reply from local receiver");
            }
          });
        });
      }
    });
  }
}
