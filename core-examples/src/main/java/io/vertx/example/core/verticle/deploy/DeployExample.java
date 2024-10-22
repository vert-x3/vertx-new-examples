package io.vertx.example.core.verticle.deploy;

import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import io.vertx.launcher.application.VertxApplication;

/*
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class DeployExample extends VerticleBase {

  public static void main(String[] args) {
    VertxApplication.main(new String[]{DeployExample.class.getName()});
  }

  @Override
  public Future<?> start() throws Exception {

    System.out.println("Main verticle has started, let's deploy some others...");

    // Different ways of deploying verticles

    // Deploy a verticle and don't wait for it to start
    vertx.deployVerticle("io.vertx.example.core.verticle.deploy.OtherVerticle");

    // Deploy another instance and  want for it to start
    vertx.deployVerticle("io.vertx.example.core.verticle.deploy.OtherVerticle")
      .compose(deploymentID -> {

        System.out.println("Other verticle deployed ok, deploymentID = " + deploymentID);

        // You can also explicitly undeploy a verticle deployment.
        // Note that this is usually unnecessary as any verticles deployed by a verticle will be automatically
        // undeployed when the parent verticle is undeployed

        return vertx
          .undeploy(deploymentID)
          .onSuccess(res2 -> {
            System.out.println("Undeployed ok!");
          });
    });

    // Deploy specifying some config
    JsonObject config = new JsonObject().put("foo", "bar");
    vertx.deployVerticle("io.vertx.example.core.verticle.deploy.OtherVerticle", new DeploymentOptions().setConfig(config));

    // Deploy 10 instances
    vertx.deployVerticle("io.vertx.example.core.verticle.deploy.OtherVerticle", new DeploymentOptions().setInstances(10));

    // Deploy it as a worker verticle
    vertx.deployVerticle("io.vertx.example.core.verticle.deploy.OtherVerticle", new DeploymentOptions().setThreadingModel(ThreadingModel.WORKER));

    return super.start();
  }
}
