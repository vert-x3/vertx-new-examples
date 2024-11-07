package io.vertx.example.virtualthreads;

import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.jdbcclient.JDBCConnectOptions;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.sqlclient.*;

import java.util.Arrays;
import java.util.List;

public class MovieRatingService extends VerticleBase {

  public static void main(String[] args) {
    var vertx = Vertx.vertx();
    vertx.deployVerticle(MovieRatingService.class, new DeploymentOptions().setThreadingModel(ThreadingModel.VIRTUAL_THREAD))
      .await();
    System.out.println("Server started");
  }

  private static final List<String> DB_STATEMENTS = Arrays.asList(
    "CREATE TABLE MOVIE (ID VARCHAR(16) PRIMARY KEY, TITLE VARCHAR(256) NOT NULL)",
    "CREATE TABLE RATING (ID INTEGER IDENTITY PRIMARY KEY, value INTEGER, MOVIE_ID VARCHAR(16))",
    "INSERT INTO MOVIE (ID, TITLE) VALUES 'starwars', 'Star Wars'",
    "INSERT INTO MOVIE (ID, TITLE) VALUES 'indianajones', 'Indiana Jones'",
    "INSERT INTO RATING (VALUE, MOVIE_ID) VALUES 1, 'starwars'",
    "INSERT INTO RATING (VALUE, MOVIE_ID) VALUES 5, 'starwars'",
    "INSERT INTO RATING (VALUE, MOVIE_ID) VALUES 9, 'starwars'",
    "INSERT INTO RATING (VALUE, MOVIE_ID) VALUES 10, 'starwars'",
    "INSERT INTO RATING (VALUE, MOVIE_ID) VALUES 4, 'indianajones'",
    "INSERT INTO RATING (VALUE, MOVIE_ID) VALUES 7, 'indianajones'",
    "INSERT INTO RATING (VALUE, MOVIE_ID) VALUES 3, 'indianajones'",
    "INSERT INTO RATING (VALUE, MOVIE_ID) VALUES 9, 'indianajones'"
  );

  private SqlClient client;

  @Override
  public Future<?> start() {
    client = JDBCPool.pool(vertx,
      new JDBCConnectOptions().setJdbcUrl("jdbc:hsqldb:mem:test?shutdown=true"),
      new PoolOptions().setMaxSize(30));

    DB_STATEMENTS.forEach(statement -> {
      client.query(statement).execute().await();
    });

    // Build Vert.x Web router
    var router = Router.router(vertx);
    router.get("/movie/:id").handler(this::getMovie);
    router.get("/rateMovie/:id").handler(this::rateMovie);
    router.get("/getRating/:id").handler(this::getRating);

    // Start the server
    return vertx.createHttpServer()
      .requestHandler(router)
      .listen(config().getInteger("http.port", 8080));
  }

  // Send info about a movie
  private void getMovie(RoutingContext ctx) {
    var id = ctx.pathParam("id");
    var rows = client.preparedQuery("SELECT TITLE FROM MOVIE WHERE ID=?").execute(Tuple.of(id)).await();
    if (rows.size() == 1) {
      ctx.response()
        .putHeader("Content-Type", "application/json")
        .end(new JsonObject()
          .put("id", id)
          .put("title", rows.iterator().next().getString("TITLE"))
          .encode());
    } else {
      ctx.response().setStatusCode(404).end();
    }
  }

  // Rate a movie
  private void rateMovie(RoutingContext ctx) {
    var movie = ctx.pathParam("id");
    var rating = Integer.parseInt(ctx.queryParam("rating").get(0));
    RowSet<Row> rows = client.preparedQuery("SELECT TITLE FROM MOVIE WHERE ID=?").execute(Tuple.of(movie)).await();
    if (rows.size() == 1) {
      client.preparedQuery("INSERT INTO RATING (VALUE, MOVIE_ID) VALUES ?, ?").execute(Tuple.of(rating, movie)).await();
      ctx.response().setStatusCode(200).end();
    } else {
      ctx.response().setStatusCode(404).end();
    }
  }

  // Get the current rating of a movie
  private void getRating(RoutingContext ctx) {
    var id = ctx.pathParam("id");
    var rows = client.preparedQuery("SELECT AVG(VALUE) AS VALUE FROM RATING WHERE MOVIE_ID=?").execute(Tuple.of(id)).await();
    ctx.response()
      .putHeader("Content-Type", "application/json")
      .end(new JsonObject()
        .put("id", id)
        .put("title", rows.iterator().next().getDouble("VALUE"))
        .encode());
  }
}
