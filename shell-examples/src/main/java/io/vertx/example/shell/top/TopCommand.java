package io.vertx.example.shell.top;

import io.vertx.core.*;
import io.vertx.ext.dropwizard.DropwizardMetricsOptions;
import io.vertx.ext.shell.ShellService;
import io.vertx.ext.shell.ShellServiceOptions;
import io.vertx.ext.shell.command.Command;
import io.vertx.ext.shell.command.CommandBuilder;
import io.vertx.ext.shell.command.CommandRegistry;
import io.vertx.ext.shell.term.TelnetTermOptions;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class TopCommand extends VerticleBase {

  public static void main(String[] args) {
    Launcher launcher = new Launcher() {
      @Override
      public void beforeStartingVertx(VertxOptions options) {
        options.setMetricsOptions(new DropwizardMetricsOptions().setEnabled(true));
      }
    };
    launcher.dispatch(new String[]{"run", TopCommand.class.getName()});
  }

  @Override
  public Future<?> start() throws Exception {

    Command starwars = CommandBuilder.command("top").
        processHandler(process -> {

          long id = process.vertx().setPeriodic(500, id_ -> {
            StringBuilder buf = new StringBuilder();
            Formatter formatter = new Formatter(buf);

            List<Thread> threads = new ArrayList<>(Thread.getAllStackTraces().keySet());
            for (int i = 1; i <= process.height(); i++) {

              // Change cursor position and erase line with ANSI escape code magic
              buf.append("\033[").append(i).append(";1H\033[K");

              //
              String format = "  %1$-5s %2$-20s %3$-50s %4$s";
              if (i == 1) {
                formatter.format(format,
                    "ID",
                    "STATE",
                    "NAME",
                    "GROUP");
              } else {
                int index = i - 2;
                if (index < threads.size()) {
                  Thread thread = threads.get(index);
                  formatter.format(format,
                      thread.getId(),
                      thread.getState().name(),
                      thread.getName(),
                      thread.getThreadGroup().getName());
                }
              }
            }

            process.write(buf.toString());
          });

          // Terminate when user hits Ctrl-C
          process.interruptHandler(v -> {
            vertx.cancelTimer(id);
            process.end();
          });

        }).build(vertx);

    ShellService service = ShellService.create(vertx, new ShellServiceOptions().setTelnetOptions(
        new TelnetTermOptions().setHost("localhost").setPort(3000)
    ));
    CommandRegistry.getShared(vertx).registerCommand(starwars);
    return service.start();
  }
}
