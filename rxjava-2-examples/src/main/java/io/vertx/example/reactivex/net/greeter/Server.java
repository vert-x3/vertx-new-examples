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

package io.vertx.example.reactivex.net.greeter;

import io.reactivex.Completable;
import io.vertx.launcher.application.VertxApplication;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.parsetools.RecordParser;

/*
 * @author Thomas Segismont
 */
public class Server extends AbstractVerticle {

  public static void main(String[] args) {
    VertxApplication.main(new String[]{Server.class.getName()});
    System.out.println("Echo server is now listening");
  }

  @Override
  public Completable rxStart() {

    return vertx.createNetServer().connectHandler(sock -> {

      RecordParser parser = RecordParser.newDelimited("\n", sock);

      parser.toFlowable()
        .map(buffer -> buffer.toString("UTF-8"))
        .doAfterTerminate(sock::close)
        .subscribe(name -> {
          if (name.isEmpty()) {
            sock.end();
          } else {
            sock.write("Hello " + name + "\n", "UTF-8");
          }
        });

    }).rxListen(1234)
      .ignoreElement();
  }
}
