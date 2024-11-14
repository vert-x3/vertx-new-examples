package io.vertx.example.grpc.loadbalancing;

import io.grpc.examples.helloworld.HelloRequest;
import io.grpc.examples.helloworld.VertxGreeterGrpcClient;
import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.net.Address;
import io.vertx.core.net.AddressResolver;
import io.vertx.core.net.SocketAddress;
import io.vertx.core.net.endpoint.LoadBalancer;
import io.vertx.grpc.client.GrpcClient;
import io.vertx.grpc.common.GrpcReadStream;
import io.vertx.launcher.application.VertxApplication;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class Client extends VerticleBase {

  // This resolver simply response localhost:808x on a service.com:80 access
  // we use such resolver here instead of DNS load balancing because there is no way to provide a local example
  // with DNS load balancing that requires multiple network interfaces
  private static final AddressResolver resolver = AddressResolver.mappingResolver(Collections
    .<Address, List<SocketAddress>>singletonMap(SocketAddress.inetSocketAddress(80, "service.com"),
      Arrays.asList(
        SocketAddress.inetSocketAddress(8080, "localhost"),
        SocketAddress.inetSocketAddress(8081, "localhost"),
        SocketAddress.inetSocketAddress(8082, "localhost")))::get);

  public static void main(String[] args) {
    VertxApplication.main(new String[]{Client.class.getName()});
  }

  private GrpcClient client;

  @Override
  public Future<?> start() throws Exception {

    // Load balancer of your choice
    LoadBalancer loadBalancer = LoadBalancer.RANDOM;

    client = GrpcClient
      .builder(vertx)
      // In reality, we could avoid using such mock resolver and the client instead could use a server list provided by a DNS server
      .withAddressResolver(resolver)
      .withLoadBalancer(loadBalancer)
      .build();

    return client.request(SocketAddress.inetSocketAddress(80, "service.com"), VertxGreeterGrpcClient.SayHello)
      .compose(request -> {
        System.out.println("Interacting with server " + request.connection().remoteAddress());
        request.end(HelloRequest.newBuilder().setName("Julien").build());
        return request.response().compose(GrpcReadStream::last);
      });
  }
}
