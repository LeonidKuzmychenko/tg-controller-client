package lk.tech.tgcontrollerclient.socket;

import lk.tech.tgcontrollerclient.utils.BaseProvider;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.Closeable;
import java.util.concurrent.TimeUnit;

@Slf4j
@NoArgsConstructor
public enum OkHttpWsClient implements Closeable {

    INSTANCE;

    private volatile String url;
    private volatile WebSocket webSocket;

    private final OkHttpClient httpClient = new OkHttpClient.Builder()
            .pingInterval(15, TimeUnit.SECONDS)
            .readTimeout(0, TimeUnit.MILLISECONDS)   // бесконечное чтение
            .retryOnConnectionFailure(true)
            .build();

    private static String buildUrl() {
        return BaseProvider.socketUrl() + "?key=" + BaseProvider.key();
    }

    // -----------------------
    // INIT
    // -----------------------
    public void init() {
        url = buildUrl();
        ReconnectManager.INSTANCE.init(this::safeConnect);
    }

    // -----------------------
    // URL UPDATE AFTER KEY CHANGE
    // -----------------------
    private void updateUrl() {
        this.url = buildUrl();
        log.info("[WS] URL updated: {}", url);
    }

    // -----------------------
    // PUBLIC API — RELOAD KEY
    // -----------------------
    public void reloadKeyAndReconnect() {
        log.info("[WS] Reloading key and reconnecting...");
        updateUrl();
        ReconnectManager.INSTANCE.reset();
        close();
        safeConnect();
    }

    // -----------------------
    // CONNECT
    // -----------------------
    public void safeConnect() {
        log.info("[WS] Connecting: {}", url);

        Request request = new Request.Builder()
                .url(url)
                .build();

        webSocket = httpClient.newWebSocket(request, new OkHttpListener());
    }

    // ===============================
    //         LISTENER
    // ===============================


    // -----------------------
    // CLOSE
    // -----------------------
    @Override
    public void close() {
        log.info("[WS] Closing WebSocket client...");

        try {
            if (webSocket != null) {
                webSocket.close(1000, "Client closing");
                webSocket = null;
            }
        } catch (Exception ignored) {}

        log.info("[WS] Closed.");
    }
}
