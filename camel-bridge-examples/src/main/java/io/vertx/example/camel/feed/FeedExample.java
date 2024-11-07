package io.vertx.example.camel.feed;

import io.vertx.camel.CamelBridge;
import io.vertx.camel.CamelBridgeOptions;
import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.Vertx;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.SimpleRegistry;

import java.util.Collections;

import static io.vertx.camel.InboundMapping.fromCamel;

/**
 * @author <a href="http://escoffier.me">Clement Escoffier</a>
 */
public class FeedExample extends VerticleBase {

  private final static String VERTX_BLOG_ATOM = "http://vertx.io/feed.xml";

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(FeedExample.class.getName());
  }


  @Override
  public Future<?> start() throws Exception {
    vertx.eventBus().consumer("announce", message -> {
      System.out.println("ANNOUNCE >> " + message.body());
    });

    vertx.eventBus().consumer("errors", message -> {
      System.out.println("ERROR >> " + message.body());
    });


    SimpleRegistry registry = new SimpleRegistry();
    registry.put("filterService", Collections.singletonMap(ReleasePostFilter.class, new ReleasePostFilter()));

    CamelContext camelContext = new DefaultCamelContext(registry);

    camelContext.addRoutes(createMyRoutes());

    camelContext.start();

    return CamelBridge.create(vertx, new CamelBridgeOptions(camelContext)
        .addInboundMapping(fromCamel("seda:announce").toVertx("announce"))
        .addInboundMapping(fromCamel("seda:errors").toVertx("errors")))
        .start();
  }


  private RouteBuilder createMyRoutes() throws Exception {
    return new RouteBuilder() {
      public void configure() throws Exception {
        errorHandler(deadLetterChannel("seda:errors"));
        // We pool the atom feeds from the source for further processing in the seda queue
        // we set the delay to 1 second for each pool.
        // Using splitEntries=true will during polling only fetch one RSS Entry at any given time.
        from("rss:" + VERTX_BLOG_ATOM +
            "?splitEntries=true&consumer.delay=100").to("seda:feeds");

        from("seda:feeds")
            // Filter
            .filter().method("filterService", "isRelease")
            // Transform (extract)
            .transform(simple("${body.entries[0].title}"))
            // Output
            .to("seda:announce");
      }
    };
  }
}
