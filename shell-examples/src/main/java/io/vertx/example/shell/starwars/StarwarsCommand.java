package io.vertx.example.shell.starwars;

import io.vertx.core.*;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;
import io.vertx.ext.dropwizard.DropwizardMetricsOptions;
import io.vertx.ext.shell.ShellService;
import io.vertx.ext.shell.ShellServiceOptions;
import io.vertx.ext.shell.command.Command;
import io.vertx.ext.shell.command.CommandBuilder;
import io.vertx.ext.shell.command.CommandRegistry;
import io.vertx.ext.shell.term.TelnetTermOptions;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class StarwarsCommand extends VerticleBase {

  public static void main(String[] args) {
    Launcher launcher = new Launcher() {
      @Override
      public void beforeStartingVertx(VertxOptions options) {
        options.setMetricsOptions(new DropwizardMetricsOptions().setEnabled(true));
      }
    };
    launcher.dispatch(new String[]{"run", StarwarsCommand.class.getName()});
  }

  @Override
  public Future<?> start() throws Exception {

    Command starwars = CommandBuilder.command("starwars").
        processHandler(process -> {

          // Connect the client
          NetClient client = process.vertx().createNetClient();
          client.connect(23, "towel.blinkenlights.nl").onComplete( ar -> {
            if (ar.succeeded()) {
              NetSocket socket = ar.result();

              // Ctrl-C closes the socket
              process.interruptHandler(v -> {
                socket.close();
              });

              //
              socket.handler(buff -> {
                // Push the data to the Shell
                process.write(buff.toString("UTF-8"));
              }).exceptionHandler(err -> {
                err.printStackTrace();
                socket.close();
              });

              // When socket closes, end the command
              socket.closeHandler(v -> {
                process.end();
              });
            } else {
              process.write("Could not connect to remote Starwars server\n").end();
            }
          });
        }).build(vertx);

    ShellService service = ShellService.create(vertx, new ShellServiceOptions().setTelnetOptions(
        new TelnetTermOptions().setHost("localhost").setPort(3000)
    ));
    CommandRegistry.getShared(vertx).registerCommand(starwars);
    return service.start();
  }
}
