// Opening the module allows Vertx to access the server-keystore.jks via the classloader
open module jpms.examples {

  requires com.fasterxml.jackson.core;

  requires io.vertx.grpc.server;

  requires io.vertx.client.sql;
  requires io.vertx.client.sql.pg;
  requires java.sql;

  requires io.netty.tcnative.classes.openssl;
  requires io.netty.internal.tcnative.openssl.osx.aarch_64;

  requires com.google.protobuf;

  // SSL
  requires jdk.crypto.ec;

  exports io.vertx.example.jpms.sqlclient;
  exports io.vertx.example.jpms.native_transport;

}
