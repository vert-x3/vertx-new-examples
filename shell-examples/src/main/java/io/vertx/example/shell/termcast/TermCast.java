package io.vertx.example.shell.termcast;

import io.vertx.core.*;
import io.vertx.ext.dropwizard.DropwizardMetricsOptions;
import io.vertx.ext.shell.term.TelnetTermOptions;
import io.vertx.ext.shell.term.TermServer;

import java.awt.*;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class TermCast extends VerticleBase {

  public static void main(String[] args) {
    Launcher launcher = new Launcher() {
      @Override
      public void beforeStartingVertx(VertxOptions options) {
        options.setMetricsOptions(new DropwizardMetricsOptions().setEnabled(true));
      }
    };
    launcher.dispatch(new String[]{"run", TermCast.class.getName()});
  }

  private TermServer termServer;

  @Override
  public Future<?> start() throws Exception {
    termServer = TermServer.createTelnetTermServer(vertx, new TelnetTermOptions().setHost("localhost").setPort(3000).setInBinary(false));
    Robot robot = new Robot();
    termServer.termHandler(term -> {
      new ScreenCaster(vertx, robot, term).handle();
    });
    return termServer.listen();
  }

  @Override
  public Future<?> stop() {
    return termServer.close();
  }
}
