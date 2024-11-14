package io.vertx.example.serviceresolver.consul;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.Vertx;
import io.vertx.core.http.*;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.SocketAddress;
import io.vertx.core.net.endpoint.LoadBalancer;
import io.vertx.serviceresolver.ServiceAddress;
import io.vertx.serviceresolver.srv.SrvResolver;
import io.vertx.serviceresolver.srv.SrvResolverOptions;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.InternetProtocol;
import org.testcontainers.containers.wait.strategy.HostPortWaitStrategy;

import java.util.ArrayList;
import java.util.List;

public class HttpClientWithConsulVerticle extends VerticleBase {

  private static final int NUM_SERVERS = 3;
  private static final int NUM_QUERIES = 10;

  public static void main(String[] args) throws Exception {

    // Start a consul container, need to expose UDP
    GenericContainer<?> container = new FixedHostPortGenericContainer<>("consul:1.9")
      .withFixedExposedPort(8500, 8500)
      .withFixedExposedPort(8600, 8600, InternetProtocol.UDP);
    container.setWaitStrategy(new HostPortWaitStrategy().forPorts(8500));
    container.start();

    Vertx vertx = Vertx.vertx();

    // Create a few services and populate consul
    HttpClientAgent client = vertx.createHttpClient();
    for (int i = 0; i < NUM_SERVERS; i++) {
      String serviceId = "app" + i;
      int servicePort = 8080 + i;
      String serviceAddress = "localhost";
      vertx.createHttpServer().requestHandler(req -> req
          .response()
          .end(serviceId))
        .listen(servicePort, serviceAddress)
        .await();
      client
        .request(HttpMethod.PUT, 8500, "localhost", "/v1/agent/service/register")
        .compose(req -> req
          .send(new JsonObject().put("ID", serviceId).put("Name", "svc").put("Address", serviceAddress).put("Port", servicePort).encode())
          .expecting(HttpResponseExpectation.SC_OK)
          .compose(HttpClientResponse::end))
        .await();
    }

    vertx.deployVerticle(new HttpClientWithConsulVerticle());
  }

  private HttpClientAgent client;

  @Override
  public Future<?> start() {

    // Default load balancer is round-robin, you can configure another one
    LoadBalancer loadBalancer;
    loadBalancer= LoadBalancer.ROUND_ROBIN;
//    loadBalancer = LoadBalancer.POWER_OF_TWO_CHOICES;

    client = vertx.httpClientBuilder()
      .withLoadBalancer(loadBalancer)
      .withAddressResolver(SrvResolver.create(new SrvResolverOptions()
        .setServer(SocketAddress.inetSocketAddress(8600, "127.0.0.1"))
        .setMinTTL(5)
      )).build();

    List<Future<?>> futs = new ArrayList<>();

    for (int i = 0; i < NUM_QUERIES; i++) {
      int idx = i;
      ServiceAddress addr = ServiceAddress.of("svc.service.consul");
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
