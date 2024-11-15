/*
 * Copyright (c) 2024, SAP SE
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 *
 */

package io.vertx.example.openapi;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.openapi.validation.RequestValidator;

public class ValidateRequest {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();

    CreateContract.createContract(vertx).map(openAPIContract -> {
        // creates the RequestValidator
        return RequestValidator.create(vertx, openAPIContract);
      }).onSuccess(requestValidator -> {
        System.out.println("RequestValidator created");
        vertx.createHttpServer().requestHandler(req ->
          // validate the request
          requestValidator.validate(req).onFailure(t -> {
            System.out.println("Request is invalid: " + t.getMessage());
            req.response().setStatusCode(400).end(t.getMessage());
          }).onSuccess(validatedRequest -> {
            System.out.println("Request is valid");
            req.response().setStatusCode(200).end();
          })
        ).listen(8080, "localhost").onSuccess(server -> {
          System.out.println("HttpServer started on port " + server.actualPort());

          WebClient.create(vertx)
            .post(server.actualPort(), "localhost", "/v1/pets")
            // send post request with a payload that does not fit to the contract
            .sendJson(new JsonObject())
            .onSuccess(response -> {
              System.out.println("Response status code expected 400:  " + response.statusCode());
              System.exit(0);
            });
        });
      }).onFailure(t -> {
        t.printStackTrace();
        System.exit(1);
      });
  }
}
