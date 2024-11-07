package io.vertx.example.mqtt.ssl;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.buffer.Buffer;
import io.vertx.launcher.application.VertxApplication;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.MqttClientOptions;

public class Client extends VerticleBase {

  private static final String MQTT_TOPIC = "/my_topic";
  private static final String MQTT_MESSAGE = "Hello Vert.x MQTT Client";
  private static final String BROKER_HOST = "localhost";
  private static final int BROKER_PORT = 8883;

  public static void main(String[] args) {
    VertxApplication.main(new String[]{Client.class.getName()});
  }

  private MqttClient mqttClient;

  @Override
  public Future<?> start() {
    MqttClientOptions options = new MqttClientOptions();
    options.setSsl(true);

    // In real life you would use a certificate and not trust any server
    options.setTrustAll(true);
    options.setHostnameVerificationAlgorithm("http");

    mqttClient = MqttClient.create(vertx, options);

    return
      mqttClient.connect(BROKER_PORT, BROKER_HOST)
      .compose(ack -> {
        System.out.println("Connected to a server");
        return mqttClient.publish(
          MQTT_TOPIC,
          Buffer.buffer(MQTT_MESSAGE),
          MqttQoS.AT_MOST_ONCE,
          false,
          false)
          .onSuccess(ignore -> System.out.println("Message published"));
      })
      .eventually(() -> mqttClient
        .disconnect()
        .onComplete(ar -> System.out.println("Disconnected from server")));
  }
}
