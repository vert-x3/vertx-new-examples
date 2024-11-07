/*
* Copyright 2018 the original author or authors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package io.vertx.example.amqp.client;

import io.vertx.amqp.*;
import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.launcher.application.VertxApplication;

import java.util.concurrent.atomic.AtomicInteger;

import static io.vertx.proton.ProtonHelper.message;

public class Sender extends VerticleBase {

  private String address = "examples";
  private AtomicInteger sent = new AtomicInteger();

  public static void main(String[] args) {
    VertxApplication.main(new String[]{Sender.class.getName()});
  }

  private AmqpClient client;
  private AmqpConnection connection;
  private AmqpSender sender;

  @Override
  public Future<?> start() throws Exception {
    AmqpClient client = AmqpClient.create(vertx, new AmqpClientOptions()
      .setPort(5672)
      .setHost("localhost"));

    return client.connect().compose(conn -> conn
      .createSender(address)
      .andThen(ar -> {
        if (ar.succeeded()) {
          connection = conn;
          sender = ar.result();

          // Schedule sending of a message every second
          System.out.println("Sender created, scheduling sends.");

          vertx.setPeriodic(1000, x -> {
            if(!sender.writeQueueFull()) {
              final int msgNum = sent.incrementAndGet();
              AmqpMessage message = AmqpMessage.create().withBody("Hello " + msgNum + " from Sender").build();

              sender.sendWithAck(message).onComplete(ack -> {
                if (ack.succeeded()) {
                  System.out.printf("Message " + msgNum + " was received by the server%n");
                } else {
                  System.out.println("Ack failed " + ack.cause().getMessage());
                }
              });

              System.out.println("Sent message: " + msgNum);
            } else {
              System.out.println("No credit to send, waiting.");
            }
          });
        } else {
          conn.close();
        }
      }));
  }
}
