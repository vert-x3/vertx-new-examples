package io.vertx.example.jpms.sqlclient;

import io.vertx.core.*;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonArray;
import io.vertx.pgclient.PgBuilder;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.sqlclient.Pool;

import java.util.stream.Collectors;

/*
 * @author <a href="mailto:pmlopes@gmail.com">Paulo Lopes</a>
 */
public class Client extends VerticleBase {

  private PgConnectOptions database;
  private HttpServer server;
  private Pool client;

  // Tested with test containers
  public Client(PgConnectOptions database) {
    this.database = database;
  }

  public Future<?> start() {
    client = PgBuilder.pool()
      .connectingTo(database)
      .using(vertx)
      .build();

    server = vertx.createHttpServer()
      .requestHandler(req -> {
        client.withConnection(conn -> conn
          .query("SELECT * FROM periodic_table")
          .collecting(Collectors.mapping(row -> row.toJson(), Collectors.toList()))
          .execute()).onComplete(ar -> {
          if (ar.succeeded()) {
            req
              .response()
              .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
              .end(new JsonArray(ar.result().value()).encode());
          } else {
            req.response()
              .setStatusCode(500)
              .end(ar.cause().toString());
          }
        });
      });

    return server.listen(8080);
  }
}
