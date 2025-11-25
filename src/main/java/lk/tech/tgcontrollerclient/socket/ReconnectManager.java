package lk.tech.tgcontrollerclient.socket;

import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;

@Slf4j
public enum ReconnectManager implements Closeable {

    INSTANCE;

    private final AtomicBoolean manualClose = new AtomicBoolean(false);
    private final AtomicBoolean reconnecting = new AtomicBoolean(false);
    private ScheduledExecutorService scheduler;
    private Runnable reconnectCallback;

    public void init(Runnable reconnectCallback) {
        this.reconnectCallback = reconnectCallback;
        this.scheduler = newSingleThreadScheduledExecutor();
        manualClose.set(false);
        reconnecting.set(false);
    }

    public void scheduleReconnect() {
        if (manualClose.get()) {
            log.info("[WS] Reconnect cancelled — manual close");
            return;
        }

        if (!reconnecting.compareAndSet(false, true)) {
            return;
        }

        log.info("[WS] Reconnecting in 3s...");

        scheduler.schedule(() -> {
            reconnecting.set(false);

            if (!manualClose.get()) {
                reconnectCallback.run();
            }

        }, 3, TimeUnit.SECONDS);
    }

    public void reset() {
        log.info("[WS] ReconnectManager: reset()");
        reconnect(false);
        scheduler = newSingleThreadScheduledExecutor();
    }

    @Override
    public void close()  {
        log.info("[WS] ReconnectManager: manual close");
        reconnect(true);
    }

    private void reconnect(boolean manual) {
        manualClose.set(manual);
        reconnecting.set(false);

        try {
            scheduler.shutdownNow();
        } catch (Exception ignored) {

        }
    }
}
