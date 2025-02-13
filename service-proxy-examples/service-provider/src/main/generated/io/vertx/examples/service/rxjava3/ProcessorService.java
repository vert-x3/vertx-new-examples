/*
 * Copyright 2014 Red Hat, Inc.
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

package io.vertx.examples.service.rxjava3;

import io.vertx.rxjava3.RxHelper;
import io.vertx.rxjava3.ObservableHelper;
import io.vertx.rxjava3.FlowableHelper;
import io.vertx.rxjava3.impl.AsyncResultMaybe;
import io.vertx.rxjava3.impl.AsyncResultSingle;
import io.vertx.rxjava3.impl.AsyncResultCompletable;
import io.vertx.rxjava3.WriteStreamObserver;
import io.vertx.rxjava3.WriteStreamSubscriber;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import io.vertx.core.Handler;
import io.vertx.core.AsyncResult;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import io.vertx.lang.rx.RxDelegate;
import io.vertx.lang.rx.RxGen;
import io.vertx.lang.rx.TypeArg;
import io.vertx.lang.rx.MappingIterator;

/**
 * The service interface.
 *
 * <p>
 * NOTE: This class has been automatically generated from the {@link io.vertx.examples.service.ProcessorService original} non RX-ified interface using Vert.x codegen.
 */

@RxGen(io.vertx.examples.service.ProcessorService.class)
public class ProcessorService implements RxDelegate {

  @Override
  public String toString() {
    return delegate.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ProcessorService that = (ProcessorService) o;
    return delegate.equals(that.delegate);
  }
  
  @Override
  public int hashCode() {
    return delegate.hashCode();
  }

  public static final TypeArg<ProcessorService> __TYPE_ARG = new TypeArg<>(    obj -> new ProcessorService((io.vertx.examples.service.ProcessorService) obj),
    ProcessorService::getDelegate
  );

  private final io.vertx.examples.service.ProcessorService delegate;
  
  public ProcessorService(io.vertx.examples.service.ProcessorService delegate) {
    this.delegate = delegate;
  }

  public ProcessorService(Object delegate) {
    this.delegate = (io.vertx.examples.service.ProcessorService)delegate;
  }

  @Override 
  public io.vertx.examples.service.ProcessorService getDelegate() {
    return delegate;
  }


  public static io.vertx.examples.service.rxjava3.ProcessorService create(io.vertx.rxjava3.core.Vertx vertx) { 
    io.vertx.examples.service.rxjava3.ProcessorService ret = io.vertx.examples.service.rxjava3.ProcessorService.newInstance((io.vertx.examples.service.ProcessorService)io.vertx.examples.service.ProcessorService.create(vertx.getDelegate()));
    return ret;
  }

  public static io.vertx.examples.service.rxjava3.ProcessorService createProxy(io.vertx.rxjava3.core.Vertx vertx, java.lang.String address) { 
    io.vertx.examples.service.rxjava3.ProcessorService ret = io.vertx.examples.service.rxjava3.ProcessorService.newInstance((io.vertx.examples.service.ProcessorService)io.vertx.examples.service.ProcessorService.createProxy(vertx.getDelegate(), address));
    return ret;
  }

  public io.reactivex.rxjava3.core.Single<io.vertx.core.json.JsonObject> process(io.vertx.core.json.JsonObject document) { 
    io.reactivex.rxjava3.core.Single<io.vertx.core.json.JsonObject> ret = rxProcess(document);
    ret = ret.cache();
    ret.subscribe(io.vertx.rxjava3.SingleHelper.nullObserver());
    return ret;
  }

  public io.reactivex.rxjava3.core.Single<io.vertx.core.json.JsonObject> rxProcess(io.vertx.core.json.JsonObject document) {
    return AsyncResultSingle.toSingle(() -> delegate.process(document), __value -> __value);
  }

  public static final int NO_NAME_ERROR = io.vertx.examples.service.ProcessorService.NO_NAME_ERROR;
  public static final int BAD_NAME_ERROR = io.vertx.examples.service.ProcessorService.BAD_NAME_ERROR;
  public static ProcessorService newInstance(io.vertx.examples.service.ProcessorService arg) {
    return arg != null ? new ProcessorService(arg) : null;
  }

}
