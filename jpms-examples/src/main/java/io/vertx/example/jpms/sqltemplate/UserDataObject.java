package io.vertx.example.jpms.sqltemplate;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.codegen.format.SnakeCase;
import io.vertx.sqlclient.templates.annotations.Column;
import io.vertx.sqlclient.templates.annotations.RowMapped;
import io.vertx.sqlclient.templates.annotations.TemplateParameter;

@DataObject
@RowMapped(formatter = SnakeCase.class)
public class UserDataObject {

  private long id;
  private String firstName;
  private String lastName;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getFirstName() {
    return firstName;
  }

  @TemplateParameter(name = "first_name")
  @Column(name = "first_name")
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  @TemplateParameter(name = "last_name")
  @Column(name = "last_name")
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }
}
