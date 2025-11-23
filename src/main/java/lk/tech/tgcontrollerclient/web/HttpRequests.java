package lk.tech.tgcontrollerclient.web;

import io.netty.buffer.ByteBufAllocator;
import lk.tech.tgcontrollerclient.BaseProvider;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

public class HttpRequests {

    private final HttpClient client;

    public HttpRequests() {
        this.client = HttpClient.create()
                .baseUrl(BaseProvider.httpUrl())
                .compress(true)
                .responseTimeout(Duration.ofSeconds(10));
    }

    // ---------------- TEXT ----------------
    public Mono<Void> sendText(String key, String command, String status) {
        System.out.println("Sending text");
        return client
                .post()
                .uri("/api/v1/answer/text/" + key + "?command=" + command + "&status=" + status)
                .response()
                .then();
    }

    // ---------------- IMAGE (PNG) ----------------
    public Mono<Void> sendImage(byte[] image, String key, String command, String status) {
        System.out.println("Sending image");
        return client
                .headers(h -> h.add("Content-Type", "image/png"))
                .post()
                .uri("/api/v1/answer/image/" + key + "?command=" + command + "&status=" + status)
                .send(Mono.just(
                        ByteBufAllocator.DEFAULT.buffer(image.length).writeBytes(image)
                ))
                .response()
                .then();
    }
}
