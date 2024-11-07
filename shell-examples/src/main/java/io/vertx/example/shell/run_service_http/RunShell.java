package io.vertx.example.shell.run_service_http;

import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.dropwizard.DropwizardMetricsOptions;
import io.vertx.ext.shell.ShellService;
import io.vertx.ext.shell.ShellServiceOptions;
import io.vertx.ext.shell.term.HttpTermOptions;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class RunShell extends VerticleBase {

  public static void main(String[] args) {
    Launcher launcher = new Launcher() {
      @Override
      public void beforeStartingVertx(VertxOptions options) {
        options.setMetricsOptions(new DropwizardMetricsOptions().setEnabled(true));
      }
    };
    launcher.dispatch(new String[]{"run", RunShell.class.getName()});
  }

  @Override
  public Future<?> start() {
    ShellService service = ShellService.create(vertx, new ShellServiceOptions().
      setHttpOptions(
        new HttpTermOptions().
          setHost("localhost").
          setPort(8080).
          setAuthOptions(new JsonObject()
            .put("provider", "properties")
            .put("config", new JsonObject()
              .put("file", "io/vertx/example/shell/run_service_http/auth.properties")))));
    return service.start();
  }
}
