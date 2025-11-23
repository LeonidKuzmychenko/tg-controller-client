package lk.tech.tgcontrollerclient.socket;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;

public class ReconnectManager {

    private final AtomicBoolean reconnecting;
    private final ScheduledExecutorService scheduler;
    private final Runnable reconnectCallback;

    public ReconnectManager(Runnable reconnectCallback) {
        this.reconnecting = new AtomicBoolean(false);
        this.scheduler = newSingleThreadScheduledExecutor();
        this.reconnectCallback = reconnectCallback;
    }

    public void scheduleReconnect() {
        if (!reconnecting.compareAndSet(false, true)) return;

        System.out.println("[WS] Reconnecting in 3s…");

        scheduler.schedule(() -> {
            reconnecting.set(false);
            reconnectCallback.run();
        }, 3, TimeUnit.SECONDS);
    }
}
