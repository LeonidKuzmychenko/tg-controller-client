package lk.tech.tgcontrollerclient.socket;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;

public class ReconnectManager {

    // Флаг: закрыт ли клиент вручную?
    private final AtomicBoolean manualClose = new AtomicBoolean(false);

    // Флаг: выполняется ли сейчас reconnect?
    private final AtomicBoolean reconnecting = new AtomicBoolean(false);

    private final ScheduledExecutorService scheduler = newSingleThreadScheduledExecutor();
    private final Runnable reconnectCallback;

    public ReconnectManager(Runnable reconnectCallback) {
        this.reconnectCallback = reconnectCallback;
    }

    /**
     * Запускает переподключение, если клиент НЕ закрыт вручную,
     * и если оно ещё не выполняется.
     */
    public void scheduleReconnect() {
        if (manualClose.get()) {
            IO.println("[WS] Reconnect cancelled — client manually closed");
            return;
        }

        if (!reconnecting.compareAndSet(false, true)) {
            return; // уже запущено
        }

        IO.println("[WS] Reconnecting in 3s...");

        scheduler.schedule(() -> {
            reconnecting.set(false);

            if (!manualClose.get()) {
                reconnectCallback.run();
            } else {
                IO.println("[WS] Reconnect skipped — manually closed");
            }

        }, 3, TimeUnit.SECONDS);
    }

    /**
     * Полностью останавливает возможность reconnect и выключает scheduler.
     */
    public void manualClose() {
        IO.println("[WS] ReconnectManager: manual close");

        manualClose.set(true);
        reconnecting.set(false);

        try {
            scheduler.shutdownNow();
        } catch (Exception ignored) {}
    }
}
