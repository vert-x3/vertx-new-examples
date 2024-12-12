package io.vertx.example.jpms.sqltemplate;

/**
 * Mapper for {@link UserDataObject}.
 * NOTE: This class has been automatically generated from the {@link UserDataObject} original class using Vert.x codegen.
 */
@io.vertx.codegen.annotations.VertxGen
public interface UserDataObjectRowMapper extends io.vertx.sqlclient.templates.RowMapper<UserDataObject> {

  UserDataObjectRowMapper INSTANCE = new UserDataObjectRowMapper() { };

  @io.vertx.codegen.annotations.GenIgnore
  java.util.stream.Collector<io.vertx.sqlclient.Row, ?, java.util.List<UserDataObject>> COLLECTOR = java.util.stream.Collectors.mapping(INSTANCE::map, java.util.stream.Collectors.toList());

  @io.vertx.codegen.annotations.GenIgnore
  default UserDataObject map(io.vertx.sqlclient.Row row) {
    UserDataObject obj = new UserDataObject();
    Object val;
    int idx;
    if ((idx = row.getColumnIndex("id")) != -1 && (val = row.getLong(idx)) != null) {
      obj.setId((long)val);
    }
    if ((idx = row.getColumnIndex("first_name")) != -1 && (val = row.getString(idx)) != null) {
      obj.setFirstName((java.lang.String)val);
    }
    if ((idx = row.getColumnIndex("last_name")) != -1 && (val = row.getString(idx)) != null) {
      obj.setLastName((java.lang.String)val);
    }
    return obj;
  }
}
