package lk.tech.tgcontrollerclient.socket;

import lk.tech.tgcontrollerclient.commands.Commands;
import lk.tech.tgcontrollerclient.dto.Answer;
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

    public ReactorWsClient(String url) {
        this.url = url;
        this.commands = new Commands();
        this.reconnectManager = new ReconnectManager(this::safeConnect);
    }

    public void safeConnect() {
        System.out.println("[WS] Connecting…");

        HttpClient.create()
                .websocket(SocketUtils.websocketClientSpec())
                .uri(url)
                .handle(this::handle)
                .doOnError(this::onError)
                .subscribe();
    }

    public Mono<Void> handle(WebsocketInbound in, WebsocketOutbound out) {
        System.out.println("[WS] Connected!");
        in.receive()
                .asByteArray()
                .publishOn(Schedulers.boundedElastic())
                .subscribe(
                        this::onSuccess,
                        this::onError,
                        reconnectManager::scheduleReconnect
                );

        return Mono.never();
    }

    public void onError(Throwable throwable) {
        System.out.println("[WS] ERROR: " + throwable);
        reconnectManager.scheduleReconnect();
    }

    private void onSuccess(byte[] bytes) {
        String text = new String(bytes, StandardCharsets.UTF_8);
        System.out.println("[WS] TEXT: " + text);
        Answer answer = commands.analyze(text);
        System.out.println("[WS] ANSWER: " + answer);
    }
}
