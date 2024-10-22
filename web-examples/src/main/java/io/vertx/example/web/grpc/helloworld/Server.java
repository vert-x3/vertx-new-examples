package io.vertx.example.web.grpc.helloworld;

import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.VertxGreeterGrpcServer;
import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.ext.web.Router;
import io.vertx.grpc.server.GrpcServer;
import io.vertx.launcher.application.VertxApplication;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class Server extends VerticleBase {

  public static void main(String[] args) {
    VertxApplication.main(new String[]{Server.class.getName()});
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
          request.response().end(HelloReply.newBuilder().setMessage(msg.getName()).build());
      });
    });

    Router router = Router.router(vertx);

    // Route gRPC to the rpc server
    router.route().consumes("application/grpc").handler(rc -> rpcServer.handle(rc.request()));


    // start the server
    return vertx
      .createHttpServer()
      .requestHandler(router)
      .listen(8080);
  }
}
