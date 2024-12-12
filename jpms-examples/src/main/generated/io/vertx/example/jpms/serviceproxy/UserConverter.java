package io.vertx.example.jpms.serviceproxy;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

/**
 * Converter and mapper for {@link io.vertx.example.jpms.serviceproxy.User}.
 * NOTE: This class has been automatically generated from the {@link io.vertx.example.jpms.serviceproxy.User} original class using Vert.x codegen.
 */
public class UserConverter {

   static void fromJson(Iterable<java.util.Map.Entry<String, Object>> json, User obj) {
    for (java.util.Map.Entry<String, Object> member : json) {
      switch (member.getKey()) {
        case "firstName":
          if (member.getValue() instanceof String) {
            obj.setFirstName((String)member.getValue());
          }
          break;
        case "lastName":
          if (member.getValue() instanceof String) {
            obj.setLastName((String)member.getValue());
          }
          break;
      }
    }
  }

   static void toJson(User obj, JsonObject json) {
    toJson(obj, json.getMap());
  }

   static void toJson(User obj, java.util.Map<String, Object> json) {
    if (obj.getFirstName() != null) {
      json.put("firstName", obj.getFirstName());
    }
    if (obj.getLastName() != null) {
      json.put("lastName", obj.getLastName());
    }
  }
}
