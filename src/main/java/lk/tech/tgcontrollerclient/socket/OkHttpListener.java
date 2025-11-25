package lk.tech.tgcontrollerclient.socket;

import lk.tech.tgcontrollerclient.services.Commands;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

@Slf4j
public class OkHttpListener extends WebSocketListener {

        @Override
        public void onOpen(WebSocket ws, Response response) {
            log.info("[WS] Connected!");
        }

        @Override
        public void onMessage(WebSocket ws, String text) {
            log.info("[WS] REQUEST: {}", text);
            Commands.INSTANCE.analyze(text);
        }

        @Override
        public void onMessage(WebSocket ws, ByteString bytes) {
            String text = bytes.utf8();
            log.info("[WS] REQUEST(BYTE): {}", text);
            Commands.INSTANCE.analyze(text);
        }

        @Override
        public void onFailure(WebSocket ws, Throwable t, Response response) {
            log.error("[WS] ERROR: ", t);
            ReconnectManager.INSTANCE.scheduleReconnect();
        }

        @Override
        public void onClosed(WebSocket ws, int code, String reason) {
            log.info("[WS] Closed: {} {}", code, reason);
        }
    }