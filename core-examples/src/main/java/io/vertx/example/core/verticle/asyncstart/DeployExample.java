package io.vertx.example.core.verticle.asyncstart;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
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

    // Deploy another instance and  want for it to start
    return vertx.deployVerticle("io.vertx.example.core.verticle.asyncstart.OtherVerticle")
      .compose(deploymentID -> {
        System.out.println("Other verticle deployed ok, deploymentID = " + deploymentID);
        return vertx
          .undeploy(deploymentID)
          .onSuccess(res2 -> {
            System.out.println("Undeployed ok!");
          });
      });
  }
}
