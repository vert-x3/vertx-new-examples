package io.vertx.example.core.net.stream;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.launcher.application.VertxApplication;

/*
 *  @author <a href="mailto:emad.albloushi@gmail.com">Emad Alblueshi</a>
 */
public class Server extends VerticleBase {

  public static void main(String[] args) {
    VertxApplication.main(new String[]{Server.class.getName()});
  }

  @Override
  public Future<?> start() throws Exception {
    return vertx
      .createNetServer()
      .connectHandler(socket -> {

        // Create batch stream for reading and writing
        BatchStream batchStream = new BatchStream(socket, socket);

        // Pause reading data
        batchStream.pause();

        // Register read stream handler
        batchStream.handler(batch -> {

            // Print received batch object from the client
            System.out.println("Server Received : " + batch.getRaw().toString());

            // Write back batch object to the client
            batchStream.write(batch);

            // Check if write queue is full
            if (batchStream.writeQueueFull()) {

              // Pause reading data
              batchStream.pause();

              // Called once write queue is ready to accept more data
              batchStream.drainHandler(done -> {

                // Resume reading data
                batchStream.resume();

              });
            }
          }).endHandler(v -> batchStream.end())
          .exceptionHandler(t -> {
            t.printStackTrace();
            batchStream.end();
          });

        // Resume reading data
        batchStream.resume();

      }).listen(1234)
      .onSuccess(v -> System.out.println("Batch server is now listening to port : 1234"));
  }
}
