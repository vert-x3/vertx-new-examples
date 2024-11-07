package io.vertx.example.shell.prompt;

import io.vertx.core.*;
import io.vertx.ext.dropwizard.DropwizardMetricsOptions;
import io.vertx.ext.shell.ShellServer;
import io.vertx.ext.shell.command.CommandResolver;
import io.vertx.ext.shell.term.TelnetTermOptions;
import io.vertx.ext.shell.term.TermServer;

import java.util.concurrent.atomic.AtomicInteger;

public class PromptCommand  extends VerticleBase {
  public static void main(String[] args) {
    Launcher launcher = new Launcher() {
      @Override
      public void beforeStartingVertx(VertxOptions options) {
        options.setMetricsOptions(new DropwizardMetricsOptions().setEnabled(true));
      }
    };
    launcher.dispatch(new String[]{"run", PromptCommand.class.getName()});
  }

  @Override
  public Future<?> start() throws Exception {

    ShellServer server = ShellServer.create(vertx);
    AtomicInteger ai = new AtomicInteger(0);
    server.shellHandler(shell -> {
      shell.setPrompt(s -> {
        try {
          return "C: " + ai.incrementAndGet();
        } catch (Exception e) {
          System.err.println("Counter");
          e.printStackTrace();
        }
        return "NOOP";
      });
    });
    TelnetTermOptions tto = new TelnetTermOptions().setPort(3000).setHost("localhost");
    TermServer telnetTermServer = TermServer.createTelnetTermServer(vertx,tto);
    server.registerTermServer(telnetTermServer);

    server.registerCommandResolver(CommandResolver.baseCommands(vertx));

    return server.listen();
  }
}
