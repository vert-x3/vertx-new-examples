package io.vertx.example.shell.wget;

import io.vertx.core.*;
import io.vertx.core.cli.Argument;
import io.vertx.core.cli.CLI;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.dropwizard.DropwizardMetricsOptions;
import io.vertx.ext.shell.ShellService;
import io.vertx.ext.shell.ShellServiceOptions;
import io.vertx.ext.shell.command.Command;
import io.vertx.ext.shell.command.CommandBuilder;
import io.vertx.ext.shell.command.CommandRegistry;
import io.vertx.ext.shell.term.TelnetTermOptions;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class WgetCommand extends VerticleBase {

  public static void main(String[] args) {
    Launcher launcher = new Launcher() {
      @Override
      public void beforeStartingVertx(VertxOptions options) {
        options.setMetricsOptions(new DropwizardMetricsOptions().setEnabled(true));
      }
    };
    launcher.dispatch(new String[]{"run", WgetCommand.class.getName()});
  }

  @Override
  public Future<?> start() {

    // Create the wget CLI
    CLI cli = CLI.create("wget").setSummary("Wget implemented with Vert.x HTTP client").
      addArgument(new Argument().setIndex(0).setArgName("http-url").setDescription("the HTTP uri to get"));

    // Create the command
    Command helloWorld = CommandBuilder.command(cli).
      processHandler(process -> {
        URL url;
        try {
          url = new URL(process.commandLine().getArgumentValue(0));
        } catch (MalformedURLException e) {
          process.write("Bad url\n").end();
          return;
        }
        HttpClient client = process.vertx().createHttpClient();
        process.write("Connecting to " + url + "\n");
        int port = url.getPort();
        if (port == -1) {
          port = 80;
        }
        client.request(HttpMethod.GET, port, url.getHost(), url.getPath())
          .compose(req -> {
            return req.send().compose(resp -> {
              process.write(resp.statusCode() + " " + resp.statusMessage() + "\n");
              String contentType = resp.getHeader("Content-Type");
              String contentLength = resp.getHeader("Content-Length");
              process.write("Length: " + (contentLength != null ? contentLength : "unspecified"));
              if (contentType != null) {
                process.write("[" + contentType + "]");
              }
              process.write("\n");
              process.end();
              return Future.succeededFuture();
            });
          }).onFailure(err -> {
          process.write("wget: error " + err.getMessage() + "\n");
          process.end();
        });
      }).build(vertx);

    ShellService service = ShellService.create(vertx, new ShellServiceOptions().setTelnetOptions(
        new TelnetTermOptions().setHost("localhost").setPort(3000)
    ));
    CommandRegistry.getShared(vertx).registerCommand(helloWorld);
    return service.start();
  }
}
