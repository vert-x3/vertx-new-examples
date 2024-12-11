package movierating

import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.coAwait

suspend fun main() {
  val vertx = Vertx.vertx()
  try {
    vertx.deployVerticle(App()).coAwait()
    println("Application started")
  } catch (exception: Throwable) {
    println("Could not start application")
    exception.printStackTrace()
  }
}

