/*
 * Copyright 2014 Red Hat, Inc.
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  and Apache License v2.0 which accompanies this distribution.
 *
 *  The Eclipse Public License is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  The Apache License v2.0 is available at
 *  http://www.opensource.org/licenses/apache2.0.php
 *
 *  You may elect to redistribute this code under either of these licenses.
 */

package io.vertx.example.web.staticsite;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.launcher.application.VertxApplication;

/**
 *
 * A simple static web server example
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class Server extends VerticleBase {

  public static void main(String[] args) {

    // We set this property to prevent Vert.x caching files loaded from the classpath on disk
    // This means if you edit the static files in your IDE then the next time they are served the new ones will
    // be served without you having to restart the main()
    // This is only useful for development - do not use this in a production server
    System.setProperty("vertx.disableFileCaching", "true");

    VertxApplication.main(new String[]{Server.class.getName()});

    System.out.println("Server is started");
  }

  @Override
  public Future<?> start() {

    Router router = Router.router(vertx);

    // Serve the static pages
    router.route().handler(StaticHandler.create("io/vertx/example/web/staticsite/webroot"));

    return vertx
      .createHttpServer()
      .requestHandler(router)
      .listen(8080);
  }

}
