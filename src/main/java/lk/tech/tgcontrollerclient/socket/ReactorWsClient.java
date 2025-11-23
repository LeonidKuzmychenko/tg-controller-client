package lk.tech.tgcontrollerclient.socket;

import lk.tech.tgcontrollerclient.utils.BaseProvider;
import lk.tech.tgcontrollerclient.services.Commands;
import lk.tech.tgcontrollerclient.utils.SocketUtils;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.websocket.WebsocketInbound;
import reactor.netty.http.websocket.WebsocketOutbound;

import java.io.Closeable;
import java.nio.charset.StandardCharsets;

public class ReactorWsClient implements Closeable {

    private final String url = BaseProvider.socketUrl() + "?key=" + BaseProvider.key();
    private final Commands commands = new Commands();
    private final ReconnectManager reconnectManager = new ReconnectManager(this::safeConnect);

    private volatile Disposable connection;

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
        commands.analyze(text);
    }

    public void close() {
        IO.println("[WS] Closing WebSocket client...");

        // 1. Отключаем автоматический reconnect
        reconnectManager.manualClose();

        // 2. Закрываем WebSocket соединение
        try {
            if (connection != null && !connection.isDisposed()) {
                connection.dispose();
            }
        } catch (Exception ignored) {}

        IO.println("[WS] Closed.");
    }
}
