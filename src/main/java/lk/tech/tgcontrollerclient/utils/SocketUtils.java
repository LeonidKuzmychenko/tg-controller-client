package lk.tech.tgcontrollerclient.utils;

import reactor.netty.http.client.WebsocketClientSpec;

public class SocketUtils {

    public static WebsocketClientSpec websocketClientSpec(){
        return WebsocketClientSpec.builder()
                .handlePing(true)
                .build();
    }
}
