/*
 * Copyright (c) 2018 Red Hat, Inc.
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

package hello;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="https://julien.ponge.org/">Julien Ponge</a>
 */
public class SampleVerticle extends VerticleBase {

  private final Logger logger = LoggerFactory.getLogger(SampleVerticle.class);

  @Override
  public Future<?> start() {
    return vertx
      .createHttpServer()
      .requestHandler(req -> {
        req.response()
          .putHeader("Content-Type", "plain/text")
          .end("Yo!");
        logger.info("Handled a request on path {} from {}", req.path(), req.remoteAddress().host());
      })
      .listen(11981);
  }
}
