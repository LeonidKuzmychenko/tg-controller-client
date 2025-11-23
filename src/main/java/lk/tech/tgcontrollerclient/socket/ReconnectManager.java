package lk.tech.tgcontrollerclient.socket;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ReconnectManager {

    private final AtomicBoolean manualClose = new AtomicBoolean(false);
    private final AtomicBoolean reconnecting = new AtomicBoolean(false);

    private final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor();

    private final Runnable reconnectCallback;

    public ReconnectManager(Runnable reconnectCallback) {
        this.reconnectCallback = reconnectCallback;
    }

    public void scheduleReconnect() {
        if (manualClose.get()) return;
        if (!reconnecting.compareAndSet(false, true)) return;

        System.out.println("[WS] Reconnecting in 3s…");

        scheduler.schedule(() -> {
            reconnecting.set(false);
            reconnectCallback.run();
        }, 3, TimeUnit.SECONDS);
    }

    public void manualClose() {
        manualClose.set(true);
        scheduler.shutdownNow();
    }
}
