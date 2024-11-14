package io.vertx.example.grpc.helloworld;

import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.HelloRequest;
import io.grpc.examples.helloworld.VertxGreeterGrpcServer;
import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.grpc.server.GrpcServer;
import io.vertx.launcher.application.VertxApplication;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class ServerWithStub extends VerticleBase {

  public static void main(String[] args) {
    VertxApplication.main(new String[]{ServerWithStub.class.getName()});
    System.out.println("Server started");
  }

  private final int port;

  public ServerWithStub(int port) {
    this.port = port;
  }

  public ServerWithStub() {
    this(8080);
  }

  @Override
  public Future<?> start() {
    VertxGreeterGrpcServer.GreeterApi service = new VertxGreeterGrpcServer.GreeterApi() {
      @Override
      public Future<HelloReply> sayHello(HelloRequest request) {
        System.out.println("Hello " + request.getName());
        return Future.succeededFuture(HelloReply.newBuilder().setMessage(request.getName()).build());
      }
    };

    // Create the server
    GrpcServer rpcServer = GrpcServer.server(vertx);

    // Bind the service
    service.bind_sayHello(rpcServer);

    // start the server
    return vertx
      .createHttpServer()
      .requestHandler(rpcServer)
      .listen(port);
  }
}
