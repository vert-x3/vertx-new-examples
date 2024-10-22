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

package io.vertx.example.core.jsonstreaming;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.file.AsyncFile;
import io.vertx.core.file.OpenOptions;
import io.vertx.core.parsetools.JsonParser;
import io.vertx.launcher.application.VertxApplication;

import java.util.concurrent.atomic.AtomicInteger;

import static io.vertx.core.parsetools.JsonEventType.VALUE;

/**
 * @author Thomas Segismont
 */
public class JsonStreamingExample extends VerticleBase {

  public static void main(String[] args) {
    VertxApplication.main(new String[]{JsonStreamingExample.class.getName()});
  }

  @Override
  public Future<?> start() throws Exception {
    vertx.fileSystem().open("large.json", new OpenOptions()).onComplete(ar -> {
      if (ar.succeeded()) {
        AsyncFile asyncFile = ar.result();

        AtomicInteger counter = new AtomicInteger();

        // Here a Json streaming parser is created wrapping an AsyncFile
        // JsonParser is a ReadStream of JsonEvent and can wrap any ReadStream of Buffer
        JsonParser jsonParser = JsonParser.newParser(asyncFile);
        // We want to parse a giant array of small obects so we switch the parser to object-value mode
        // The parser will then emit objects inside the array as a single value event
        // This is pretty convenient for the developer
        jsonParser.objectValueMode()
          .exceptionHandler(t -> {
            t.printStackTrace();
            asyncFile.close();
          })
          .endHandler(v -> {
            System.out.println("Done!");
            asyncFile.close();
          }).handler(event -> {
          // Got a new JsonEvent
          // In object-value mode the event should always be of type "VALUE"
          if (event.type() == VALUE) {
            // Use mapTo to map the JSON obect to a Java class
            DataPoint dataPoint = event.mapTo(DataPoint.class);
            // Let's not log all objects from this giant file...
            if (counter.incrementAndGet() % 100 == 0) {
              System.out.println("DataPoint = " + dataPoint);
            }
          }
        });
      } else {
        ar.cause().printStackTrace();
      }
    });

    return super.start();
  }
}
