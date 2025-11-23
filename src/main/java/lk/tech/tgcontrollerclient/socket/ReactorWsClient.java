package lk.tech.tgcontrollerclient.socket;

import lk.tech.tgcontrollerclient.commands.Commands;
import lk.tech.tgcontrollerclient.dto.Result;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.websocket.WebsocketInbound;
import reactor.netty.http.websocket.WebsocketOutbound;

import java.nio.charset.StandardCharsets;

public class ReactorWsClient {

    private final Commands commands;
    private final ReconnectManager reconnectManager;
    private final String url;

    private volatile Disposable connection;

    public ReactorWsClient(String url) {
        this.url = url;
        this.commands = new Commands();
        this.reconnectManager = new ReconnectManager(this::safeConnect);
    }

    public void safeConnect() {
        IO.println("[WS] Connecting...");

        connection = HttpClient.create()
                .websocket(SocketUtils.websocketClientSpec())
                .uri(url)
                .handle(this::handle)
                .doOnError(this::onError)
                .subscribe();
    }

    private Mono<Void> handle(WebsocketInbound in, WebsocketOutbound out) {
        IO.println("[WS] Connected!");

        return in.receive()
                .asByteArray()
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(this::onSuccess)
                .doOnError(this::onError)
                .then();
    }

    private void onError(Throwable error) {
        IO.println("[WS] ERROR: " + error);
        if (connection != null) {
            connection.dispose();
        }
        reconnectManager.scheduleReconnect();
    }

    private void onSuccess(byte[] bytes) {
        String text = new String(bytes, StandardCharsets.UTF_8);
        IO.println("[WS] REQUEST: " + text);
        Result result = commands.analyze(text);
        IO.println("[WS] RESULT: " + result);
    }
}
