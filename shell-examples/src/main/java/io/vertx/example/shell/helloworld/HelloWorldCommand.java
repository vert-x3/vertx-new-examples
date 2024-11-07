package io.vertx.example.shell.helloworld;

import io.vertx.core.*;
import io.vertx.ext.dropwizard.DropwizardMetricsOptions;
import io.vertx.ext.shell.ShellService;
import io.vertx.ext.shell.ShellServiceOptions;
import io.vertx.ext.shell.command.Command;
import io.vertx.ext.shell.command.CommandBuilder;
import io.vertx.ext.shell.command.CommandRegistry;
import io.vertx.ext.shell.term.TelnetTermOptions;

/*
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class HelloWorldCommand extends VerticleBase {

  public static void main(String[] args) {
    Launcher launcher = new Launcher() {
      @Override
      public void beforeStartingVertx(VertxOptions options) {
        options.setMetricsOptions(new DropwizardMetricsOptions().setEnabled(true));
      }
    };
    launcher.dispatch(new String[]{"run", HelloWorldCommand.class.getName()});
  }

  @Override
  public Future<?> start() {

    Command helloWorld = CommandBuilder.command("hello-world").
        processHandler(process -> {
          process.write("hello world\n");
          process.end();
        }).build(vertx);

    ShellService service = ShellService.create(vertx, new ShellServiceOptions().setTelnetOptions(
        new TelnetTermOptions().setHost("localhost").setPort(3000)
    ));
    CommandRegistry.getShared(vertx).registerCommand(helloWorld);
    return service.start();
  }
}
