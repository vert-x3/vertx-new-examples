package io.vertx.example.grpc.deadline;

import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.HelloRequest;
import io.grpc.examples.helloworld.VertxGreeterGrpcClient;
import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.net.SocketAddress;
import io.vertx.grpc.client.GrpcClient;
import io.vertx.grpc.client.GrpcClientOptions;
import io.vertx.grpc.common.GrpcErrorException;
import io.vertx.grpc.common.GrpcStatus;
import io.vertx.launcher.application.VertxApplication;

import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class ClientWithStub extends VerticleBase {

  public static void main(String[] args) {
    VertxApplication.main(new String[]{ClientWithStub.class.getName()});
  }

  private GrpcClient client;

  @Override
  public Future<?> start() {
    client = GrpcClient.client(vertx, new GrpcClientOptions()
      .setTimeout(5)
      .setTimeoutUnit(TimeUnit.SECONDS));
    VertxGreeterGrpcClient stub = new VertxGreeterGrpcClient(client, SocketAddress.inetSocketAddress(8080, "localhost"));
    HelloRequest request = HelloRequest.newBuilder().setName("Julien").build();
    System.out.println("Sending a request that should timeout due to the server deadline");
    return stub
      .sayHello(request)
      .map(HelloReply::getMessage)
      .recover(err -> {
        System.out.println("Timeout as expected");
        return Future.succeededFuture("Expected timeout");
      });
  }
}
