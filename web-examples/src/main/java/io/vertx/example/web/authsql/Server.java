package io.vertx.example.web.authsql;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.ext.auth.prng.VertxContextPRNG;
import io.vertx.ext.auth.sqlclient.SqlAuthentication;
import io.vertx.ext.auth.sqlclient.SqlAuthenticationOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.*;
import io.vertx.ext.web.sstore.LocalSessionStore;
import io.vertx.jdbcclient.JDBCConnectOptions;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.launcher.application.VertxApplication;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/*
 * @author <a href="http://tfox.org">Tim Fox</a>
 * @author <a href="mailto:pmlopes@gmail.com">Paulo Lopes</a>
 */
public class Server extends VerticleBase {

  public static void main(String[] args) {
    VertxApplication.main(new String[]{Server.class.getName()});
  }

  @Override
  public Future<?> start() throws Exception {

    // Create a JDBC client with a test database
    Pool client = JDBCPool.pool(
      vertx,
      new JDBCConnectOptions().setJdbcUrl("jdbc:hsqldb:mem:test?shutdown=true"),
      new PoolOptions());

    // Simple auth service which uses a JDBC data source
    SqlAuthentication authProvider = SqlAuthentication.create(client, new SqlAuthenticationOptions());

    // quick load of test data, this is a *sync* helper not intended for
    // real deployments...
    setUpInitialData("jdbc:hsqldb:mem:test?shutdown=true", authProvider);

    Router router = Router.router(vertx);

    // We need sessions and request bodies
    router.route().handler(BodyHandler.create());
    router.route().handler(SessionHandler.create(LocalSessionStore.create(vertx)));

    // Any requests to URI starting '/private/' require login
    router.route("/private/*").handler(RedirectAuthHandler.create(authProvider, "/loginpage.html"));

    // Serve the static private pages from directory 'private'
    router.route("/private/*").handler(StaticHandler.create("io/vertx/example/web/authsql/private").setCachingEnabled(false));

    // Handles the actual login
    router.route("/loginhandler").handler(FormLoginHandler.create(authProvider));

    // Implement logout
    router.route("/logout").handler(context -> {
      context.userContext().logout();
      // Redirect back to the index page
      context.response().putHeader("location", "/").setStatusCode(302).end();
    });

    // Serve the non-private static pages
    router.route().handler(StaticHandler.create("io/vertx/example/web/authsql/webroot"));

    return vertx
      .createHttpServer()
      .requestHandler(router)
      .listen(8080);
  }

  private Connection conn;

  private void setUpInitialData(String url, SqlAuthentication sqlAuth) throws SQLException {
    conn = DriverManager.getConnection(url);
    executeStatement("drop table if exists users;");
    executeStatement("drop table if exists user_roles;");
    executeStatement("drop table if exists roles_perms;");
    executeStatement("CREATE TABLE users (\n" +
      " username VARCHAR(255) NOT NULL,\n" +
      " password VARCHAR(255) NOT NULL\n" +
      ");");
    executeStatement("CREATE TABLE users_roles (\n" +
      " username VARCHAR(255) NOT NULL,\n" +
      " role VARCHAR(255) NOT NULL\n" +
      ");");
    executeStatement("CREATE TABLE roles_perms (\n" +
      " role VARCHAR(255) NOT NULL,\n" +
      " perm VARCHAR(255) NOT NULL\n" +
      ");");

    String hash = sqlAuth.hash(
      "pbkdf2", // hashing algorithm (OWASP recommended)
      VertxContextPRNG.current().nextString(32), // secure random salt
      "sausages" // password
    );

    executeStatement("insert into users values ('tim', '" + hash + "');");
    executeStatement("insert into users_roles values ('tim', 'dev');");
    executeStatement("insert into users_roles values ('tim', 'admin');");
    executeStatement("insert into roles_perms values ('dev', 'commit_code');");
    executeStatement("insert into roles_perms values ('dev', 'eat_pizza');");
    executeStatement("insert into roles_perms values ('admin', 'merge_pr');");
  }

  private void executeStatement(String sql) throws SQLException {
    conn.createStatement().execute(sql);
  }

}

