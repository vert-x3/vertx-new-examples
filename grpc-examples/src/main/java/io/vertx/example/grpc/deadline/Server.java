package io.vertx.example.grpc.deadline;

import io.grpc.examples.helloworld.VertxGreeterGrpcServer;
import io.vertx.core.Future;
import io.vertx.core.Timer;
import io.vertx.core.VerticleBase;
import io.vertx.grpc.server.GrpcServer;
import io.vertx.grpc.server.GrpcServerOptions;
import io.vertx.launcher.application.VertxApplication;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class Server extends VerticleBase {

  public static void main(String[] args) {
    VertxApplication.main(new String[]{Server.class.getName()});
    System.out.println("Server started");
  }

  @Override
  public Future<?> start() {
    // Create the server
    GrpcServer rpcServer = GrpcServer.server(vertx, new GrpcServerOptions()
      .setScheduleDeadlineAutomatically(true));

    // The rpc service
    rpcServer.callHandler(VertxGreeterGrpcServer.SayHello, request -> {
      Timer deadline = request.deadline();
      if (deadline != null) {
        System.out.println("This request has a deadline");
        deadline.onSuccess(v -> {
          System.out.println("Deadline fired");
        });
      }
      request
        .last()
        .onSuccess(msg -> {
          // Do not send a response to trigger timeout
      });
    });

    // start the server
    return vertx
      .createHttpServer()
      .requestHandler(rpcServer)
      .listen(8080);
  }
}
