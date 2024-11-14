package io.vertx.example.grpc.deadline;

import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.HelloRequest;
import io.grpc.examples.helloworld.VertxGreeterGrpcServer;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.VerticleBase;
import io.vertx.grpc.server.GrpcServer;
import io.vertx.grpc.server.GrpcServerOptions;
import io.vertx.launcher.application.VertxApplication;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class ServerWithStub extends VerticleBase {

  public static void main(String[] args) {
    VertxApplication.main(new String[]{ServerWithStub.class.getName()});
    System.out.println("Server started");
  }

  @Override
  public Future<?> start() {
    VertxGreeterGrpcServer.GreeterApi service = new VertxGreeterGrpcServer.GreeterApi() {
      @Override
      public Future<HelloReply> sayHello(HelloRequest request) {
        // Do not send a response to trigger timeout
        return Promise.<HelloReply>promise().future();
      }
    };

    // Create the server
    GrpcServer rpcServer = GrpcServer.server(vertx, new GrpcServerOptions().setScheduleDeadlineAutomatically(true));

    // Bind the service
    service.bind_sayHello(rpcServer);

    // start the server
    return vertx
      .createHttpServer()
      .requestHandler(rpcServer)
      .listen(8080);
  }
}
