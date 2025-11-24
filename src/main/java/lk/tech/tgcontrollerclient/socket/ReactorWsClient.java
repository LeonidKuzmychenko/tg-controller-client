package lk.tech.tgcontrollerclient.socket;

import lk.tech.tgcontrollerclient.services.Commands;
import lk.tech.tgcontrollerclient.utils.BaseProvider;
import lk.tech.tgcontrollerclient.utils.SocketUtils;
import lombok.NoArgsConstructor;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.websocket.WebsocketInbound;
import reactor.netty.http.websocket.WebsocketOutbound;

import java.io.Closeable;
import java.nio.charset.StandardCharsets;

@NoArgsConstructor
public enum ReactorWsClient implements Closeable {

    INSTANCE;

    private volatile String url;
    private volatile Disposable connection;

    private static String buildUrl() {
        return BaseProvider.socketUrl() + "?key=" + BaseProvider.key();
    }

    public void init() {
        url = buildUrl();
        ReconnectManager.INSTANCE.init(this::safeConnect);
    }

    // -----------------------
    // URL UPDATE AFTER KEY CHANGE
    // -----------------------
    private void updateUrl() {
        this.url = buildUrl();
        IO.println("[WS] URL updated: " + url);
    }

    // -----------------------
    //  PUBLIC API — RELOAD KEY
    // -----------------------
    public void reloadKeyAndReconnect() {
        IO.println("[WS] Reloading key and reconnecting...");
        updateUrl();
        ReconnectManager.INSTANCE.reset();
        close();
        safeConnect();
    }

    // -----------------------
    //  CONNECT
    // -----------------------
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

    private void onSuccess(byte[] bytes) {
        String text = new String(bytes, StandardCharsets.UTF_8);
        IO.println("[WS] REQUEST: " + text);
        Commands.INSTANCE.analyze(text);
    }

    private void onError(Throwable error) {
        IO.println("[WS] ERROR: " + error);

        if (connection != null) {
            connection.dispose();
        }

        ReconnectManager.INSTANCE.scheduleReconnect();
    }

    @Override
    public void close() {
        IO.println("[WS] Closing WebSocket client...");

        try {
            if (connection != null && !connection.isDisposed()) {
                connection.dispose();
            }
        } catch (Exception ignored) {}

        IO.println("[WS] Closed.");
    }
}
