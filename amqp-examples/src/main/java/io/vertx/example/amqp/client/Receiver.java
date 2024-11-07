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

import io.vertx.amqp.AmqpClient;
import io.vertx.amqp.AmqpClientOptions;
import io.vertx.amqp.AmqpConnection;
import io.vertx.amqp.AmqpReceiver;
import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.launcher.application.VertxApplication;

public class Receiver extends VerticleBase {

  private String address = "examples";

  public static void main(String[] args) {
    VertxApplication.main(new String[]{Receiver.class.getName()});
  }

  private AmqpClient client;
  private AmqpConnection connection;
  private AmqpReceiver receiver;

  @Override
  public Future<?> start() {
    client = AmqpClient.create(vertx, new AmqpClientOptions()
      .setPort(5672)
      .setHost("localhost")
    );

    return client.connect().compose(conn -> conn
      .createReceiver(address)
      .andThen(ar -> {
      if (ar.succeeded()) {
        connection = conn;
        receiver = ar.result();

        receiver.handler(msg -> {
          System.out.println("Received message with content: " + msg.bodyAsString());
        });
      } else {
        conn.close();
      }
    }))
      .onSuccess(v -> System.out.println("Received created"));
  }

  @Override
  public Future<?> stop() {
    return client.close();
  }
}
