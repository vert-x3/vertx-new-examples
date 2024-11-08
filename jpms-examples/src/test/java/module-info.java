open module jpms.examples.tests {

  requires jpms.examples;
  requires org.junit.jupiter.api;
  requires testcontainers;
  requires io.vertx.core;
  requires io.vertx.sql.client;
  requires io.vertx.sql.client.pg;

  // Brought by testcontainers and required to make Idea happy
  requires junit;
  requires org.slf4j;
}
