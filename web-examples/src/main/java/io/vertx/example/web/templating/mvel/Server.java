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

package io.vertx.example.web.templating.mvel;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.TemplateHandler;
import io.vertx.ext.web.templ.mvel.MVELTemplateEngine;
import io.vertx.launcher.application.VertxApplication;

import static io.vertx.ext.web.handler.TemplateHandler.DEFAULT_CONTENT_TYPE;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class Server extends VerticleBase {

  public static void main(String[] args) {
    VertxApplication.main(new String[]{Server.class.getName()});
  }

  @Override
  public Future<?> start() {

    Router router = Router.router(vertx);

    // Serve the dynamic pages
    MVELTemplateEngine templateEngine = MVELTemplateEngine.create(vertx);
    TemplateHandler templateHandler = TemplateHandler.create(templateEngine, "io/vertx/example/web/templating/mvel/templates", DEFAULT_CONTENT_TYPE);

    router.route("/dynamic/*")
      .handler(ctx -> {
        // put the context into the template render context
        ctx.put("context", ctx);
        ctx.next();
      })
      .handler(templateHandler);

    // Serve the static pages
    router.route().handler(StaticHandler.create("io/vertx/example/web/templating/mvel/webroot"));

    return vertx
      .createHttpServer()
      .requestHandler(router)
      .listen(8080);
  }

}
