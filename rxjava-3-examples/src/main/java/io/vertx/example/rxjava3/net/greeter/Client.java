/*
 * Copyright 2017 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package io.vertx.example.rxjava3.net.greeter;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.vertx.launcher.application.VertxApplication;
import io.vertx.rxjava3.core.AbstractVerticle;
import io.vertx.rxjava3.core.net.NetSocket;
import io.vertx.rxjava3.core.parsetools.RecordParser;

import java.util.stream.Stream;

/*
 * @author Thomas Segismont
 */
public class Client extends AbstractVerticle {

  public static void main(String[] args) {
    VertxApplication.main(new String[]{Client.class.getName()});
  }

  @Override
  public Completable rxStart() {
    Single<NetSocket> sub = vertx.createNetClient().rxConnect(1234, "localhost");

    return sub.flatMapCompletable(socket -> {
      RecordParser parser = RecordParser.newDelimited("\n", socket.toFlowable());

      // Now send some data
      Stream.of("John", "Joe", "Lisa", "Bill").forEach(name -> {
        System.out.println("Net client sending: " + name);
        socket.write(name);
        socket.write("\n");
      });

      // Tell the server we are done
      socket.write("\n");

      return parser
        .toFlowable()
        .map(buffer -> buffer.toString("UTF-8"))
        .doOnNext(greeting -> System.out.println("Net client receiving: " + greeting))
        .ignoreElements();
    });
  }
}
