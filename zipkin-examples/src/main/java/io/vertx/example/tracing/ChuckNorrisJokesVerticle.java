package io.vertx.example.tracing;

import io.vertx.core.*;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonArray;
import io.vertx.pgclient.PgBuilder;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.sqlclient.*;

import java.util.ArrayList;
import java.util.List;

public class ChuckNorrisJokesVerticle extends VerticleBase implements Handler<HttpServerRequest> {

  private final PgConnectOptions options;
  private Pool pool;

  public ChuckNorrisJokesVerticle(PgConnectOptions options) {
    this.options = options;
  }

  @Override
  public void handle(HttpServerRequest request) {
    pool
      .query("select joke from jokes ORDER BY random() limit 1")
      .execute().onComplete(res -> {
        if (res.succeeded() && res.result().size() > 0) {
          Row row = res.result().iterator().next();
          String joke = row.getString(0);
          request.response().putHeader("content-type", "text/plain").end(joke);
        } else {
          request.response().setStatusCode(500).end("No jokes available");
        }
      });
  }

  @Override
  public Future<?> start() {

    pool = PgBuilder.pool()
      .with(new PoolOptions().setMaxSize(5))
      .connectingTo(options)
      .using(vertx)
      .build();

    HttpServer server = vertx.createHttpServer().requestHandler(this);

    Future<RowSet<Row>> insert = pool.query("create table jokes(joke varchar(255))").execute()
      .compose(v -> vertx.fileSystem().readFile("jokes.json"))
      .compose(buffer -> {
        JsonArray array = new JsonArray(buffer);
        List<Tuple> batch = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
          String joke = array.getJsonObject(i).getString("joke");
          batch.add(Tuple.of(joke));
        }
        return pool
          .preparedQuery("insert into jokes values ($1)")
          .executeBatch(batch);
      });

    return insert
      .compose(ignore -> server.listen(8082));
  }
}
