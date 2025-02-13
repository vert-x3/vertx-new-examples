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

package io.vertx.example.rxjava3.services.serviceproxy.rxjava3;

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


@RxGen(io.vertx.example.rxjava3.services.serviceproxy.SomeDatabaseService.class)
public class SomeDatabaseService implements RxDelegate {

  @Override
  public String toString() {
    return delegate.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    SomeDatabaseService that = (SomeDatabaseService) o;
    return delegate.equals(that.delegate);
  }
  
  @Override
  public int hashCode() {
    return delegate.hashCode();
  }

  public static final TypeArg<SomeDatabaseService> __TYPE_ARG = new TypeArg<>(    obj -> new SomeDatabaseService((io.vertx.example.rxjava3.services.serviceproxy.SomeDatabaseService) obj),
    SomeDatabaseService::getDelegate
  );

  private final io.vertx.example.rxjava3.services.serviceproxy.SomeDatabaseService delegate;
  
  public SomeDatabaseService(io.vertx.example.rxjava3.services.serviceproxy.SomeDatabaseService delegate) {
    this.delegate = delegate;
  }

  public SomeDatabaseService(Object delegate) {
    this.delegate = (io.vertx.example.rxjava3.services.serviceproxy.SomeDatabaseService)delegate;
  }

  @Override 
  public io.vertx.example.rxjava3.services.serviceproxy.SomeDatabaseService getDelegate() {
    return delegate;
  }


  public io.reactivex.rxjava3.core.Single<io.vertx.core.json.JsonObject> getDataById(int id) { 
    io.reactivex.rxjava3.core.Single<io.vertx.core.json.JsonObject> ret = rxGetDataById(id);
    ret = ret.cache();
    ret.subscribe(io.vertx.rxjava3.SingleHelper.nullObserver());
    return ret;
  }

  public io.reactivex.rxjava3.core.Single<io.vertx.core.json.JsonObject> rxGetDataById(int id) {
    return AsyncResultSingle.toSingle(() -> delegate.getDataById(id), __value -> __value);
  }

  public static SomeDatabaseService newInstance(io.vertx.example.rxjava3.services.serviceproxy.SomeDatabaseService arg) {
    return arg != null ? new SomeDatabaseService(arg) : null;
  }

}
