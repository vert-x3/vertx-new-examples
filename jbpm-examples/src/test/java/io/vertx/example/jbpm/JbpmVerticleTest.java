package io.vertx.example.jbpm;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.file.FileSystemOptions;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
public class JbpmVerticleTest {

  Vertx vertx;

  @BeforeEach
  void prepare() {
    vertx = Vertx.vertx(new VertxOptions()
      .setMaxEventLoopExecuteTime(1000)
      .setPreferNativeTransport(true)
      .setFileSystemOptions(new FileSystemOptions().setFileCachingEnabled(true)));
  }

  @Test
  @DisplayName("Deploy JbpmVerticles")
  void deployJbpmVerticle(VertxTestContext testContext) {
    vertx.deployVerticle(new JbpmVerticle(), testContext.succeeding(id -> testContext.completeNow()));
    vertx.deployVerticle(new MsgSenderVerticle(), testContext.succeeding(id -> testContext.completeNow()));
  }

}
