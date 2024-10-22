package movierating

import coroutineHandler
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.jdbcclient.JDBCConnectOptions
import io.vertx.jdbcclient.JDBCPool
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.coAwait
import io.vertx.sqlclient.Pool
import io.vertx.sqlclient.PoolOptions
import io.vertx.sqlclient.Tuple


class App : CoroutineVerticle() {

  private lateinit var client: Pool

  override suspend fun start() {

    client = JDBCPool.pool(vertx, JDBCConnectOptions().setJdbcUrl("jdbc:hsqldb:mem:test?shutdown=true"), PoolOptions().setMaxSize(30))

    // Populate database
    val statements = listOf(
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
    )
    statements.forEach {
      client.query(it).execute().coAwait();
    }

    // Build Vert.x Web router
    val router = Router.router(vertx)
    router.get("/movie/:id").coroutineHandler { ctx -> getMovie(ctx) }
    router.post("/rateMovie/:id").coroutineHandler { ctx -> rateMovie(ctx) }
    router.get("/getRating/:id").coroutineHandler { ctx -> getRating(ctx) }

    // Start the server
    vertx.createHttpServer()
      .requestHandler(router)
      .listen(config.getInteger("http.port", 8080))
      .coAwait()
  }

  // Send info about a movie
  suspend fun getMovie(ctx: RoutingContext) {
    val id = ctx.pathParam("id")
    val rows = client.preparedQuery("SELECT TITLE FROM MOVIE WHERE ID=?").execute(Tuple.of(id)).coAwait()
    if (rows.size() == 1) {
      ctx.response().end(json {
        obj("id" to id, "title" to rows.iterator().next().getString("TITLE")).encode()
      })
    } else {
      ctx.response().setStatusCode(404).end()
    }
  }

  // Rate a movie
  suspend fun rateMovie(ctx: RoutingContext) {
    val movie = ctx.pathParam("id")
    val rating = Integer.parseInt(ctx.queryParam("rating")[0])
    val rows = client.preparedQuery("SELECT TITLE FROM MOVIE WHERE ID=?").execute(Tuple.of(movie)).coAwait()
    if (rows.size() == 1) {
      client.preparedQuery("INSERT INTO RATING (VALUE, MOVIE_ID) VALUES ?, ?").execute(Tuple.of(rating, movie)).coAwait()
      ctx.response().setStatusCode(200).end()
    } else {
      ctx.response().setStatusCode(404).end()
    }
  }

  // Get the current rating of a movie
  suspend fun getRating(ctx: RoutingContext) {
    val id = ctx.pathParam("id")
    val rows = client.preparedQuery("SELECT AVG(VALUE) AS VALUE FROM RATING WHERE MOVIE_ID=?").execute(Tuple.of(id)).coAwait()
    ctx.response().end(json {
      obj("id" to id, "rating" to rows.iterator().next().getDouble("VALUE")).encode()
    })
  }
}
