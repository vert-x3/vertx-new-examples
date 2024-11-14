package io.vertx.example.grpc.deadline;

import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.HelloRequest;
import io.grpc.examples.helloworld.VertxGreeterGrpcClient;
import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.net.SocketAddress;
import io.vertx.grpc.client.GrpcClient;
import io.vertx.grpc.client.GrpcClientOptions;
import io.vertx.grpc.common.GrpcReadStream;
import io.vertx.launcher.application.VertxApplication;

import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class Client extends VerticleBase {

  public static void main(String[] args) {
    VertxApplication.main(new String[]{Client.class.getName()});
  }

  private GrpcClient client;

  @Override
  public Future<?> start() throws Exception {
    client = GrpcClient.client(vertx, new GrpcClientOptions()
      .setTimeout(5)
      .setTimeoutUnit(TimeUnit.SECONDS));
    return client.request(SocketAddress.inetSocketAddress(8080, "localhost"), VertxGreeterGrpcClient.SayHello)
      .compose(request -> {
        System.out.println("Sending a request that should timeout due to the server deadline");
        request.end(HelloRequest.newBuilder().setName("Julien").build());
        return request.response().compose(GrpcReadStream::last);
      })
      .map(HelloReply::getMessage)
      .recover(err -> {
        System.out.println("Timeout as expected");
        return Future.succeededFuture("Expected timeout");
      });
  }
}
