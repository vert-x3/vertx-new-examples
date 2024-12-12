// Opening the module allows Vertx to access the server-keystore.jks via the classloader
open module jpms.examples {

  requires com.fasterxml.jackson.core;

  requires io.vertx.grpc.server;

  requires io.vertx.sql.client;
  requires io.vertx.sql.client.pg;
  requires java.sql;

  requires static io.vertx.serviceproxy;

  requires io.netty.tcnative.classes.openssl;
  requires io.netty.internal.tcnative.openssl.osx.aarch_64;

  requires com.google.protobuf;

  // Brotli4J
  requires com.aayushatharva.brotli4j;

  // SSL
  requires jdk.crypto.ec;
  requires io.netty.codec.compression;

  // Service proxy
  requires io.vertx.codegen.api;
  requires io.vertx.codegen.json;

  exports io.vertx.example.jpms.sqlclient;
  exports io.vertx.example.jpms.native_transport;
  exports io.vertx.example.jpms.serviceproxy;

}
