= Vert.x 5 examples

This repository contains a range of examples, so you can get up and running easily with Vert.x. 5

Vert.x 4 examples can be found here https://github.com/vert-x3/vertx-examples/tree/4.x

== The examples

The examples demonstrate how to use all different bits of Vert.x including Vert.x core, Vert.x-Web and various other services and features.

Most of the examples have been written as verticles. For more information on verticles please consult the Vert.x core manual.

Using verticles allows you to scale and run your code from the command line more easily, but if you prefer the same code in the verticles can also be run embedded.
It's really up to you.
Verticles are entirely optional in Vert.x.

=== Running the examples

==== Running in your IDE

Most of the Java examples can be run directly in your IDE.

We provide a `main()` method in most of the example classes which allows you to conveniently run them directly in your IDE.

Just right-click the main method or class in your IDE and run as... application (supported by all major IDEs).

=== Vert.x core examples

The link:core-examples/README.adoc[Vert.x core examples] contains a wide range of examples using just Vert.x Core.

=== Service resolver examples

The link:service-resolver-examples/README.adoc[Vert.x Service Resolver] examples contain client
using a resolver to discover service endpoints.

=== Vert.x-Web examples

Vert.x-Web is a toolkit for building web applications using Vert.x

The link:web-examples/README.adoc[Vert.x-Web examples] contains a wide range of examples using Vert.x-Web

=== Vert.x Web Client examples

Vert.x Web Client that provides an easy-to-use web client for Vert.x.

The link:web-client-examples/README.adoc[Vert.x Web Client examples] contains a wide range of examples using the Vert.x Web Client

=== Virtual Threads

The `virtual-threads-examples` module shows how to use Vert.x with virtual threads.

See link:virtual-threads-examples/README.adoc[Vert.x Virtual Threads 5 examples].

=== Vert.x JUnit 5 examples

The `vertx-junit5` modules allow testing Vert.x asynchronous operations with JUnit 5.

See link:junit5-examples/README.adoc[Vert.x JUnit 5 examples].

=== Vert.x Zipkin examples

link:zipkin-examples/README.adoc[Vert.x Zipkin examples] contains a wide range of examples using Vert.x Zipkin.

=== RxJava examples

Vert.x for RxJava provides most of its APIs as RxJava, so you can use those if you prefer.

RxJava is a great choice when you want to perform complex operations on multiple asynchronous streams of data.

The link:rxjava-2-examples/README.adoc[Vert.x RxJava 2] examples contains a wide range of examples using Vert.x for RxJava 2
The link:rxjava-3-examples/README.adoc[Vert.x RxJava 3] examples contains a wide range of examples using Vert.x for RxJava 3

=== gRPC examples

The link:grpc-examples/README.adoc[Vert.x gRPC Examples] show how you can implement a gRPC service and invoke a gRPC service with Vert.x.

=== Kotlin coroutines examples

The link:kotlin-coroutines-examples/README.md[Kotlin coroutines example] is a sample JDBC application demonstrating Kotlin coroutines in action.

=== Mail examples

The link:mail-examples/README.adoc[Vert.x Mail examples]
contains a few examples using Vert.x Mail

The mail examples show different ways to create the mail message and send it via
tls, ssl etc. The examples either use `localhost:25` to send a mail or use host
`mail.example.com`. To actually run the examples you will have to change the
mail server and the user credentials in the `MailLogin` example.

=== Service Proxy Examples

The link:service-proxy-examples/README.adoc[Vert.x Service Proxy examples] contains an example of service proxy usage.
It depicts how a service provider can be implemented and how the published service can be consumed.

=== Vert.x AMQP examples

The link:amqp-examples/README.adoc[Vert.x AMQP examples] shows how to interact with an AMQP broker.

=== Spring Examples

The link:spring-examples/README.adoc[Vert.x Spring Examples] shows how vert.x application can be integrated inside a Spring
ecosystem.

=== Redis example

The link:redis-examples/README.adoc[Vert.x Redis Example] shows how you can interact with Redis using the vert.x Redis client.

=== Mongo example

The link:mongo-examples/README.adoc[Vert.x Mongo Example] shows how you can interact with MongoDB using the vert.x Mongo client.

=== SQL client example

The link:sql-client-examples/README.adoc[Vert.x SQL Client Examples] shows how you can interact PostgreSQL or MySQL Databases using the Reactive SQL client.

=== Kafka example

The link:kafka-examples/README.adoc[Vert.x Kafka Examples] shows how you can interact Kafka using the vert.x Kafka
client.

=== MQTT example

The link:mqtt-examples/README.adoc[Vert.x MQTT Examples] shows how you can write MQTT servers and clients using Vert.x MQTT.

=== Service Proxy Examples

The link:service-proxy-examples/README.adoc[Vert.x Service Proxy examples] contains an example of service proxy usage.
It depicts how a service provider can be implemented and how the published service can be consumed.

=== JPMS examples

This link:jpms-examples/README.adoc[JPMS examples] shows how a simple Java (11) modular application using Vert.x.

=== Camel Bridge examples

The link:camel-bridge-examples/README.adoc[Vert.x Camel Bridge Examples] show how you can use Apache Camel routes from
the event bus.

=== GraphQL examples

The link:web-graphql-examples/README.adoc[Vert.x Web GraphQL] examples contain simple client/server GraphQL applications built with https://vertx.io/docs/vertx-web-graphql/java/[Vert.x Web GraphQL] and the https://www.graphql-java.com/[GraphQL-Java] library.
