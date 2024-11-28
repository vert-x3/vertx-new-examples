package io.vertx.example.proxy.websocket;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.http.WebSocketClient;
import io.vertx.core.http.WebSocketConnectOptions;
import io.vertx.launcher.application.VertxApplication;

public class Client extends VerticleBase {

    public static void main(String[] args) {
        VertxApplication.main(new String[]{Client.class.getName()});
    }

    private WebSocketClient client;

    @Override
    public Future<?> start() throws Exception {
        client = vertx.createWebSocketClient();
        return client.connect(new WebSocketConnectOptions().setHost("localhost").setPort(8080))
                .onSuccess(ws -> ws.textMessageHandler(msg -> {
                    System.out.println("Received message: " + msg);
                }));
    }
}
