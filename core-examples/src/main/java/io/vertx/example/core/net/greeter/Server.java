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

package io.vertx.example.core.net.greeter;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.parsetools.RecordParser;
import io.vertx.launcher.application.VertxApplication;

/*
 * @author Thomas Segismont
 */
public class Server extends VerticleBase {

  public static void main(String[] args) {
    VertxApplication.main(new String[]{Server.class.getName()});
  }

  @Override
  public Future<?> start() throws Exception {

    return vertx
      .createNetServer()
      .connectHandler(sock -> {

        RecordParser parser = RecordParser.newDelimited("\n", sock);

        parser
          .endHandler(v -> sock.close())
          .exceptionHandler(t -> {
            t.printStackTrace();
            sock.close();
          })
          .handler(buffer -> {
            String name = buffer.toString("UTF-8");
            sock.write("Hello " + name + "\n", "UTF-8");
          });

      }).listen(1234)
      .onSuccess(v -> System.out.println("Echo server is now listening"));
  }
}
