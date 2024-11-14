package io.vertx.example.grpc.helloworld;

import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.VertxGreeterGrpcServer;
import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.grpc.server.GrpcServer;
import io.vertx.launcher.application.VertxApplication;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class Server extends VerticleBase {

  public static void main(String[] args) {
    VertxApplication.main(new String[]{Server.class.getName()});
    System.out.println("Server started");
  }

  private final int port;

  public Server(int port) {
    this.port = port;
  }

  public Server() {
    this(8080);
  }

  @Override
  public Future<?> start() {
    // Create the server
    GrpcServer rpcServer = GrpcServer.server(vertx);

    // The rpc service
    rpcServer.callHandler(VertxGreeterGrpcServer.SayHello, request -> {
      request
        .last()
        .onSuccess(msg -> {
          System.out.println("Hello " + msg.getName());
          request.response().end(HelloReply.newBuilder().setMessage(msg.getName()).build());
      });
    });

    // start the server
    return vertx
      .createHttpServer()
      .requestHandler(rpcServer)
      .listen(port);
  }
}
