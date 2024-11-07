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

package io.vertx.example.mqtt.ssl;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.net.PemKeyCertOptions;
import io.vertx.launcher.application.VertxApplication;
import io.vertx.mqtt.MqttServer;
import io.vertx.mqtt.MqttServerOptions;

/**
 * An example of using the MQTT server with TLS support
 */
public class Server extends VerticleBase {

  public static void main(String[] args) {
    VertxApplication.main(new String[]{Server.class.getName()});
  }

  @Override
  public Future<?> start() {

    MqttServerOptions options = new MqttServerOptions()
      .setPort(8883)
      .setKeyCertOptions(new PemKeyCertOptions()
        .setKeyPath("io/vertx/example/mqtt/ssl/server-key.pem")
        .setCertPath("io/vertx/example/mqtt/ssl/server-cert.pem"))
      .setSsl(true);

    MqttServer mqttServer = MqttServer.create(vertx, options);

    return mqttServer
      .endpointHandler(endpoint -> {

        // shows main connect info
        System.out.println("MQTT client [" + endpoint.clientIdentifier() + "] request to connect, " +
          "clean session = " + endpoint.isCleanSession());

        // accept connection from the remote client
        endpoint.accept(false);

      })
      .listen().onSuccess(ar -> System.out.println("MQTT server is listening on port " + mqttServer.actualPort()));
  }
}
