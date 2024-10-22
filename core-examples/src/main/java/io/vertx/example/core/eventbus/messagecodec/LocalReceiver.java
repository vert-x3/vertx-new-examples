package io.vertx.example.core.eventbus.messagecodec;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.eventbus.EventBus;
import io.vertx.example.core.eventbus.messagecodec.util.CustomMessage;

/**
 * Local receiver
 * @author Junbong
 */
public class LocalReceiver extends VerticleBase {

  @Override
  public Future<?> start() throws Exception {
    EventBus eventBus = vertx.eventBus();

    // Does not have to register codec because sender already registered
    /*eventBus.registerDefaultCodec(CustomMessage.class, new CustomMessageCodec());*/

    // Receive message
    eventBus.consumer("local-message-receiver", message -> {
      CustomMessage customMessage = (CustomMessage) message.body();

      System.out.println("Custom message received: "+customMessage.getSummary());

      // Replying is same as publishing
      CustomMessage replyMessage = new CustomMessage(200, "a00000002", "Message sent from local receiver!");
      message.reply(replyMessage);
    });

    return super.start();
  }
}
