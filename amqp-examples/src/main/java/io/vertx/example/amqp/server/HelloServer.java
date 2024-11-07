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
package io.vertx.example.amqp.server;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.Vertx;
import io.vertx.launcher.application.VertxApplication;
import io.vertx.proton.ProtonConnection;
import io.vertx.proton.ProtonReceiver;
import io.vertx.proton.ProtonSender;
import io.vertx.proton.ProtonServer;
import org.apache.qpid.proton.amqp.messaging.AmqpValue;
import org.apache.qpid.proton.amqp.messaging.Section;
import org.apache.qpid.proton.amqp.transport.AmqpError;
import org.apache.qpid.proton.amqp.transport.ErrorCondition;
import org.apache.qpid.proton.message.Message;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static io.vertx.proton.ProtonHelper.message;

/**
 * HelloServer
 * <p>
 * Allows attaching senders and receivers to any address, printing the messages
 * received from producers, and periodically sending any consumers a message.
 */

public class HelloServer extends VerticleBase {

  private static final int PORT = 5672;

  public static void main(String[] args) {
    VertxApplication.main(new String[]{HelloServer.class.getName()});
  }

  @Override
  public Future<?> start() throws Exception {
    ProtonServer server = ProtonServer.create(vertx);

    // Configure how new connections are handled
    server.connectHandler((connection) -> {
      initConnection(vertx, connection);
    });

    return Future
      .<ProtonServer>future(promise -> server.listen(PORT, promise::handle))
      .onSuccess(v -> System.out.println("Listening on port " + v.actualPort()));
  }

  // Initialise then open new connections
  private static void initConnection(Vertx vertx, ProtonConnection connection) {
    connection.openHandler(res -> {
      System.out.println("Client connection opened, container-id: " + connection.getRemoteContainer());
      connection.open();
    });

    connection.closeHandler(c -> {
      System.out.println("Client closing connection, container-id: " + connection.getRemoteContainer());
      connection.close();
      connection.disconnect();
    });

    connection.disconnectHandler(c -> {
      System.out.println("Client socket disconnected, container-id: " + connection.getRemoteContainer());
      connection.disconnect();
    });

    connection.sessionOpenHandler(session -> {
      session.closeHandler(x -> {
        session.close();
        session.free();
      });
      session.open();
    });

    connection.senderOpenHandler(sender -> {
      initSender(vertx, connection, sender);
    });

    connection.receiverOpenHandler(HelloServer::initReceiver);
  }

  // Initialise then open new sender (when a client receiver/consumer attaches)
  private static void initSender(Vertx vertx, ProtonConnection connection, ProtonSender sender) {
    org.apache.qpid.proton.amqp.messaging.Source remoteSource = (org.apache.qpid.proton.amqp.messaging.Source) sender.getRemoteSource();
    if (remoteSource == null) {
      // Example doesn't support 'looking up' existing links, so we will just close with an error
      sender.setTarget(null);
      sender.setCondition(new ErrorCondition(AmqpError.INVALID_FIELD, "No source terminus specified"));
      sender.open();
      sender.close();
      return;
    }

    // Configure the servers local source+target details.
    // Just reflecting the remote details (+ set dynamic address if requested).
    // This is rather naive, for example use only, proper servers should
    // ensure that they advertise their own Source settings which actually
    // reflect what is in place.
    if (remoteSource.getDynamic()) {
      String dynamicAddress = UUID.randomUUID().toString();
      remoteSource.setAddress(dynamicAddress);
    }

    sender.setSource(remoteSource);
    sender.setTarget(sender.getRemoteTarget());

    // Can optionally add a sendQueueDrainHandler to await receiver
    // granting credit. Here we will just schedule sends to happen
    // periodically assuming there is credit available at the time.
    AtomicInteger sent = new AtomicInteger();
    final long timer = vertx.setPeriodic(1000, t -> {
      if (connection.isDisconnected()) {
        vertx.cancelTimer(t);
      } else {
        if (!sender.sendQueueFull()) {
          int msgNum = sent.incrementAndGet();
          System.out.println("Sending message " + msgNum + " to client, for address: " + remoteSource.getAddress());
          Message m = message("Hello " + msgNum + " from Server!");
          sender.send(m, delivery -> {
            System.out.println("Message " + msgNum + " was received by the client.");
          });
        }
      }
    });

    sender.detachHandler(x -> {
      vertx.cancelTimer(timer);
      sender.detach();
      sender.free();
    });

    sender.closeHandler(x -> {
      vertx.cancelTimer(timer);
      sender.close();
      sender.free();
    });

    sender.open();
  }

  // Initialise then open new receiver (when a client sender/producer attaches)
  private static void initReceiver(ProtonReceiver receiver) {
    org.apache.qpid.proton.amqp.messaging.Target remoteTarget = (org.apache.qpid.proton.amqp.messaging.Target) receiver.getRemoteTarget();
    if (remoteTarget == null) {
      // Example doesn't support 'looking up' existing links, so we will just close with an error.
      receiver.setTarget(null);
      receiver.setCondition(new ErrorCondition(AmqpError.INVALID_FIELD, "No target terminus specified"));
      receiver.open();
      receiver.close();
      return;
    }

    // Configure the servers local source+target details.
    // Just reflecting the remote details (+ set dynamic address if requested).
    // This is rather naive, for example use only, proper servers should
    // ensure that they advertise their own Target settings which actually
    // reflect what is in place.
    if (remoteTarget.getDynamic()) {
      String dynamicAddress = UUID.randomUUID().toString();
      remoteTarget.setAddress(dynamicAddress);
    }

    receiver.setTarget(remoteTarget);
    receiver.setSource(receiver.getRemoteSource());

    // Handle arriving messages. Just prints out their details.
    // Unless configured otherwise, the receiver automatically accepts messages when the
    // handler returns if another disposition hasn't been applied, and also grants
    // credit when opened and replenishes it as messages are received.
    receiver.handler((delivery, msg) -> {
      String address = remoteTarget.getAddress();
      if (address == null) {
        address = msg.getAddress();
      }

      Section body = msg.getBody();
      if (body instanceof AmqpValue) {
        String content = (String) ((AmqpValue) body).getValue();
        System.out.println("Received message for address: " + address + ", body: " + content);
      }
    });

    receiver.detachHandler(x -> {
      receiver.detach();
      receiver.free();
    });

    receiver.closeHandler(x -> {
      receiver.close();
      receiver.free();
    });

    receiver.open();
  }
}
