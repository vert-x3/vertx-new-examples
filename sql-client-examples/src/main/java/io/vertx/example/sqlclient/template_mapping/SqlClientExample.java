package io.vertx.example.sqlclient.template_mapping;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.Vertx;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlConnectOptions;
import io.vertx.sqlclient.SqlResult;
import io.vertx.sqlclient.templates.SqlTemplate;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

/*
 * @author <a href="mailto:pmlopes@gmail.com">Paulo Lopes</a>
 */
public class SqlClientExample extends VerticleBase {

  // Convenience method so you can run it in your IDE
  public static void main(String[] args) {
    PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>();
    postgres.start();
    PgConnectOptions options = new PgConnectOptions()
      .setPort(postgres.getMappedPort(5432))
      .setHost(postgres.getContainerIpAddress())
      .setDatabase(postgres.getDatabaseName())
      .setUser(postgres.getUsername())
      .setPassword(postgres.getPassword());
    // Uncomment for MySQL
//    MySQLContainer<?> mysql = new MySQLContainer<>();
//    mysql.start();
//    MySQLConnectOptions options = new MySQLConnectOptions()
//      .setPort(mysql.getMappedPort(3306))
//      .setHost(mysql.getContainerIpAddress())
//      .setDatabase(mysql.getDatabaseName())
//      .setUser(mysql.getUsername())
//      .setPassword(mysql.getPassword());
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new SqlClientExample(options));
  }

  private final SqlConnectOptions options;
  private Pool pool;

  public SqlClientExample(SqlConnectOptions options) {
    this.options = options;
  }

  @Override
  public Future<?> start() {

    pool = Pool.pool(vertx, options, new PoolOptions().setMaxSize(4));

    // create a SQL template for inserting users
    SqlTemplate<User, SqlResult<Void>> insertTemplate = SqlTemplate
      .forUpdate(pool, "insert into users values (#{id}, #{first_name}, #{last_name})").mapFrom(UserParametersMapper.INSTANCE);

    // create a SQL template for querying users
    SqlTemplate<Map<String, Object>, RowSet<User>> queryTemplate = SqlTemplate
      .forQuery(pool, "select * from users where id = #{id}").mapTo(UserRowMapper.INSTANCE);

    // create a test table
    return pool.query("create table users(id int primary key, first_name varchar(255), last_name varchar(255))")
      .execute()
      .compose(r ->
        // insert some test data
        insertTemplate.executeBatch(Arrays.asList(
          new User().setId(1).setFirstName("Dale").setLastName("Cooper"),
          new User().setId(2).setFirstName("Sherlock").setLastName("Holmes")
        ))
      ).compose(r ->
      // query some data with arguments
      queryTemplate.execute(Collections.singletonMap("id", 2))
    ).onSuccess(users -> {
      for (User user : users) {
        System.out.println("user = " + user);
      }
    });
  }
}
