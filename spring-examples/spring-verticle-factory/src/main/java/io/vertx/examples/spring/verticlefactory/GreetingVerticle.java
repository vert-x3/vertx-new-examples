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

package io.vertx.examples.spring.verticlefactory;

import io.vertx.core.*;
import io.vertx.core.http.HttpServerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

/**
 * @author Thomas Segismont
 */
@Component
// Prototype scope is needed as multiple instances of this verticle will be deployed
@Scope(SCOPE_PROTOTYPE)
public class GreetingVerticle extends VerticleBase implements Handler<HttpServerRequest> {
  private static final Logger LOG = LoggerFactory.getLogger(GreetingVerticle.class);

  @Autowired
  Greeter greeter;

  @Override
  public void handle(HttpServerRequest request) {
    String name = request.getParam("name");
    LOG.info("Got request for name: " + name);
    if (name == null) {
      request.response().setStatusCode(400).end("Missing name");
    } else {
      // It's fine to call the greeter from the event loop as it's not blocking
      request.response().end(greeter.sayHello(name));
    }
  }

  @Override
  public Future<?> start() {
    return vertx
      .createHttpServer()
      .requestHandler(this)
      .listen(8080)
      .onSuccess(ar -> {
        LOG.info("GreetingVerticle started: @" + this.hashCode());
      });
  }
}
