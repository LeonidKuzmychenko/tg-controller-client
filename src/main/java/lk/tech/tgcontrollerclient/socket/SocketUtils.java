package lk.tech.tgcontrollerclient.socket;

import reactor.netty.http.client.WebsocketClientSpec;

public class SocketUtils {

    public static WebsocketClientSpec websocketClientSpec(){
        return WebsocketClientSpec.builder()
                .maxFramePayloadLength(20 * 1024 * 1024)
                .handlePing(true)
                .build();
    }
}
