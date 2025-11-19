package lk.tech;

import jakarta.websocket.*;
import lk.tech.commands.Commands;
import lk.tech.dto.Answer;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

@ClientEndpoint
public class DesktopClient {

    private volatile Session session;
    private Commands commands;
    private String serverUrl;
    private String key;

    private final ScheduledExecutorService reconnectExecutor =
            Executors.newSingleThreadScheduledExecutor();

    private final AtomicBoolean reconnecting = new AtomicBoolean(false);
    private final AtomicBoolean manualClose = new AtomicBoolean(false);

    private final WebSocketContainer container;

    // ---------------- CONSTRUCTOR ----------------
    public DesktopClient() {
        container = ContainerProvider.getWebSocketContainer();
        container.setDefaultMaxBinaryMessageBufferSize(20 * 1024 * 1024);
        container.setDefaultMaxTextMessageBufferSize(20 * 1024 * 1024);
    }

    // ---------------- CONNECT ----------------
    public void connect(String serverUrl, String key) {
        this.serverUrl = serverUrl;
        this.key = key;

        commands = new Commands();
        safeConnect();
    }

    private void safeConnect() {
        if (manualClose.get()) return;

        try {
            System.out.println("[WebSocket] Connecting to server...");

            reconnecting.set(false);  // <-- ключевое исправление

            session = container.connectToServer(this, URI.create(serverUrl + "?key=" + key));

            System.out.println("[WebSocket] Connected!");
            reconnecting.set(false);

        } catch (Exception e) {
            System.out.println("[WebSocket] Connect failed: " + e.getMessage());
            scheduleReconnect();
        }
    }

    // ---------------- RECONNECT ----------------
    private void scheduleReconnect() {

        // если уже идёт пытка reconnect → ничего не делаем
        if (!reconnecting.compareAndSet(false, true)) {
            System.out.println("[WebSocket] Reconnect already scheduled");
            return;
        }

        System.out.println("[WebSocket] Scheduling reconnect in 3s...");

        reconnectExecutor.schedule(() -> {
            if (manualClose.get()) return; // Exit was pressed
            System.out.println("[WebSocket] Reconnecting...");
            safeConnect();
        }, 3, TimeUnit.SECONDS);
    }

    // ---------------- EVENTS ----------------
    @OnOpen
    public void onOpen(Session session) {
        System.out.println("[WebSocket] onOpen");
    }

    @OnMessage
    public void onMessage(String msg) throws Exception {
        Answer answer = commands.analyze(msg);
        Object data = answer.getData();

        if (data instanceof String s) {
            sendText(new ObjectMapper().writeValueAsString(answer));
            return;
        }

        if (data instanceof List<?> list) {
            for (Object value : list) {
                if (value instanceof byte[] bytes) {
                    byte[] prefix = (answer.getCommand() + ":").getBytes(StandardCharsets.UTF_8);
                    sendBinary(prefix, bytes);
                }
            }
        }
    }

    @OnError
    public void onError(Session session, Throwable thr) {
        thr.printStackTrace();
        System.out.println("[WebSocket] ERROR: " + thr.getMessage());
        scheduleReconnect();
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        System.out.println("[WebSocket] CLOSED: " + reason);

        // Если закрыто вручную — не делаем reconnect
        if (manualClose.get()) return;

        scheduleReconnect();
    }

    // ---------------- SEND ----------------
    public void sendText(String text) throws Exception {
        if (session != null && session.isOpen()) {
            session.getBasicRemote().sendText(text);
        } else {
            System.out.println("[WebSocket] Cannot send text — session closed");
        }
    }

    public void sendBinary(byte[] prefix, byte[] bytes) throws Exception {
        if (session == null || !session.isOpen()) {
            System.out.println("[WebSocket] Cannot send binary — session closed");
            return;
        }

        ByteBuffer buffer = ByteBuffer.allocate(prefix.length + bytes.length);
        buffer.put(prefix);
        buffer.put(bytes);
        buffer.flip();

        session.getBasicRemote().sendBinary(buffer);
        System.out.println("[WebSocket] Sent binary (" + bytes.length + " bytes)");
    }

    // ---------------- CLOSE ----------------
    public void close() {
        try {
            manualClose.set(true);

            if (session != null) session.close();
            reconnectExecutor.shutdownNow();

        } catch (Exception ignored) {
        }
    }
}
