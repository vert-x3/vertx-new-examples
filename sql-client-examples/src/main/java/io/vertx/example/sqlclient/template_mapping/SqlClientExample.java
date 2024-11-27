package io.vertx.example.sqlclient.template_mapping;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.sqlclient.*;
import io.vertx.sqlclient.templates.SqlTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

/*
 * @author <a href="mailto:pmlopes@gmail.com">Paulo Lopes</a>
 */
public abstract class SqlClientExample extends VerticleBase {

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
