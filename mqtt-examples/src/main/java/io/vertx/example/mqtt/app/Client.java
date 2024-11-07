/*
 * Copyright 2016 Red Hat Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.vertx.example.mqtt.app;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.launcher.application.VertxApplication;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.MqttClientOptions;

import java.nio.charset.Charset;

/**
 * An example of using the MQTT client
 */
public class Client extends VerticleBase {

  private static final String MQTT_TOPIC = "/my_topic";
  private static final String MQTT_MESSAGE = "Hello Vert.x MQTT Client";
  private static final String BROKER_HOST = "localhost";
  private static final int BROKER_PORT = 1883;

  public static void main(String[] args) {
    VertxApplication.main(new String[]{Client.class.getName()});
  }

  private MqttClient client;

  @Override
  public Future<?> start() {
    MqttClientOptions options = new MqttClientOptions().setKeepAliveInterval(2);

    MqttClient client = MqttClient.create(Vertx.vertx(), options);

    // handler will be called when we have a message in topic we subscribing for
    client.publishHandler(publish -> {
      System.out.println("Just received message on [" + publish.topicName() + "] payload [" + publish.payload().toString(Charset.defaultCharset()) + "] with QoS [" + publish.qosLevel() + "]");
    });

    // handle response on subscribe request
    client.subscribeCompletionHandler(h -> {
      System.out.println("Receive SUBACK from server with granted QoS : " + h.grantedQoSLevels());

      // let's publish a message to the subscribed topic
      client.publish(
        MQTT_TOPIC,
        Buffer.buffer(MQTT_MESSAGE),
        MqttQoS.AT_MOST_ONCE,
        false,
        false)
        .onComplete(s -> System.out.println("Publish sent to a server"));

      // unsubscribe from receiving messages for earlier subscribed topic
      vertx.setTimer(5000, l -> client.unsubscribe(MQTT_TOPIC));
    });

    // handle response on unsubscribe request
    client.unsubscribeCompletionHandler(h -> {
      System.out.println("Receive UNSUBACK from server");
      vertx.setTimer(5000, l ->
        // disconnect for server
        client.disconnect().onComplete(d -> System.out.println("Disconnected form server"))
      );
    });

    // connect to a server
    return client.connect(BROKER_PORT, BROKER_HOST)
      .compose(conn -> {
      System.out.println("Connected to a server");
      return client.subscribe(MQTT_TOPIC, 0);
    });
  }
}
