/*
 * Copyright 2017 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package io.vertx.examples.spring.verticlefactory;

import java.util.Map;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.spi.VerticleFactory;

import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author Thomas Segismont
 */
@Configuration
@ComponentScan("io.vertx.examples.spring.verticlefactory")
public class ExampleApplication {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();

    ApplicationContext context = new AnnotationConfigApplicationContext(ExampleApplication.class);

    VerticleFactory verticleFactory = context.getBean(SpringVerticleFactory.class);

    // The verticle factory is registered manually because it is created by the Spring container
    vertx.registerVerticleFactory(verticleFactory);

    // Scale the verticles on cores: create 4 instances during the deployment
    DeploymentOptions options = new DeploymentOptions().setInstances(4);
    Map<String, AbstractVerticle> verticleBeans = context.getBeansOfType(AbstractVerticle.class);
    verticleBeans.forEach((beanName, bean) -> {
      vertx.deployVerticle(verticleFactory.prefix() + ":" + AopProxyUtils.ultimateTargetClass(bean).getName(), options);
    });
  }
}
