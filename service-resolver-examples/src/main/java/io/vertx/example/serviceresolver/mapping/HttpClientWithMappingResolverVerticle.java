package io.vertx.example.serviceresolver.mapping;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.Vertx;
import io.vertx.core.http.*;
import io.vertx.core.net.Address;
import io.vertx.core.net.AddressResolver;
import io.vertx.core.net.SocketAddress;
import io.vertx.core.net.endpoint.LoadBalancer;
import io.vertx.serviceresolver.ServiceAddress;

import java.util.*;

public class HttpClientWithMappingResolverVerticle extends VerticleBase {

  private static final int NUM_SERVERS = 3;
  private static final int NUM_QUERIES = 10;

  public static void main(String[] args) {

    Vertx vertx = Vertx.vertx();

    // Create a few services and populate consul
    List<SocketAddress> addressList = new ArrayList<>();
    for (int i = 0; i < NUM_SERVERS; i++) {
      String serviceId = "svc" + i;
      int servicePort = 8080 + i;
      String serviceAddress = "localhost";
      HttpServer server = vertx.createHttpServer().requestHandler(req -> req
          .response()
          .end(serviceId))
        .listen(servicePort, serviceAddress)
        .await();
      addressList.add(SocketAddress.inetSocketAddress(server.actualPort(), "localhost"));
    }

    // Create a custom resolver that uses a map to provide service addresses
    Map<Address, List<SocketAddress>> customMapping = Collections.singletonMap(ServiceAddress.of("svc"), addressList);
    AddressResolver resolver = AddressResolver.mappingResolver(customMapping::get);

    vertx.deployVerticle(new HttpClientWithMappingResolverVerticle(resolver));
  }

  private final AddressResolver resolver;
  private HttpClientAgent client;

  public HttpClientWithMappingResolverVerticle(AddressResolver resolver) {
    this.resolver = resolver;
  }

  @Override
  public Future<?> start() {

    // Default load balancer is round-robin, you can configure another one
    LoadBalancer loadBalancer;
    loadBalancer= LoadBalancer.ROUND_ROBIN;
//    loadBalancer = LoadBalancer.POWER_OF_TWO_CHOICES;

    client = vertx.httpClientBuilder()
      .withLoadBalancer(loadBalancer)
      .withAddressResolver(resolver)
      .build();

    List<Future<?>> futs = new ArrayList<>();

    for (int i = 0; i < NUM_QUERIES; i++) {
      int idx = i;
      ServiceAddress addr = ServiceAddress.of("svc");
      Future<?> fut = client.request(new RequestOptions().setServer(addr)).compose(req -> req
        .send()
        .expecting(HttpResponseExpectation.SC_OK)
        .compose(HttpClientResponse::body)).andThen(ar -> {
        if (ar.succeeded()) {
          System.out.println(idx + " -> " + ar.result());
        }
      });
      futs.add(fut);
    }

    return Future.all(futs);
  }
}
