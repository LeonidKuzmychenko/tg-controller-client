package lk.tech.tgcontrollerclient.socket;

import io.netty.buffer.Unpooled;
import lk.tech.tgcontrollerclient.commands.Commands;
import lk.tech.tgcontrollerclient.dto.Answer;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.client.WebsocketClientSpec;
import reactor.netty.http.websocket.WebsocketOutbound;
import tools.jackson.databind.ObjectMapper;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ReactorWsClient {

    private final Commands commands = new Commands();
    private final ReconnectManager reconnectManager =
            new ReconnectManager(this::safeConnect);

    private String url;
    private Disposable connection;
    private WebsocketOutbound outbound;

    // ------------------------ CONNECT ------------------------

    public void connect(String url) {
        this.url = url;
        safeConnect();
    }

    private void safeConnect() {
        System.out.println("[WS] Connecting…");

        HttpClient base = HttpClient.create();

        connection = base
                .websocket(WebsocketClientSpec.builder()
                        .maxFramePayloadLength(20 * 1024 * 1024)
                        .handlePing(true)
                        .build())
                .uri(url)
                .handle((in, out) -> {

                    this.outbound = out;
                    System.out.println("[WS] Connected!");

                    in.receive()
                            .asByteArray()
                            .publishOn(Schedulers.boundedElastic())
                            .subscribe(
                                    this::handleIncomingBytes,
                                    e -> {
                                        System.out.println("[WS] ERROR: " + e);
                                        reconnectManager.scheduleReconnect();
                                    },
                                    reconnectManager::scheduleReconnect
                            );

                    return Mono.never();
                })
                .doOnError(err -> {
                    System.out.println("[WS] Connect failed: " + err.getMessage());
                    reconnectManager.scheduleReconnect();
                })
                .subscribe();
    }

    // ------------------------ HANDLE INCOMING ------------------------

    private void handleIncomingBytes(byte[] bytes) {
        String text = new String(bytes, StandardCharsets.UTF_8);
        onTextMessage(text);
    }

    protected void onTextMessage(String text) {
        System.out.println("[WS] TEXT: " + text);

        Answer answer = commands.analyze(text);
        Object data = answer.getData();

        try {
            if (data instanceof String s) {
                sendText(new ObjectMapper().writeValueAsString(answer));
                return;
            }

            if (data instanceof List<?> list) {
                for (Object value : list) {
                    if (value instanceof byte[] bytes) {

                        byte[] prefix = (answer.getCommand() + ":")
                                .getBytes(StandardCharsets.UTF_8);

                        ByteBuffer buffer = ByteBuffer.allocate(prefix.length + bytes.length);
                        buffer.put(prefix);
                        buffer.put(bytes);
                        buffer.flip();

                        sendBinary(buffer.array());
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ------------------------ SEND ------------------------

    public void sendText(String text) {
        if (outbound == null) {
            System.out.println("[WS] Cannot send text — not connected");
            return;
        }
        outbound.sendString(Mono.just(text)).then().subscribe();
    }

    public void sendBinary(byte[] bytes) {
        if (outbound == null) {
            System.out.println("[WS] Cannot send binary — not connected");
            return;
        }
        outbound.send(Mono.just(Unpooled.wrappedBuffer(bytes)))
                .then()
                .subscribe();
        System.out.println("[WS] Sent binary (" + bytes.length + " bytes)");
    }

    // ------------------------ CLOSE ------------------------

    public void close() {
        reconnectManager.manualClose();
        if (connection != null) connection.dispose();
    }
}
