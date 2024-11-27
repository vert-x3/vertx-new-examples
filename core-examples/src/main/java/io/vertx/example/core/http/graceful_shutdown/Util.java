package io.vertx.example.core.http.graceful_shutdown;

import java.time.Instant;

class Util {
  static void log(String msg) {
    System.out.println(Instant.now() + " " + msg);
  }

  private Util() {
    // Utility
  }
}
