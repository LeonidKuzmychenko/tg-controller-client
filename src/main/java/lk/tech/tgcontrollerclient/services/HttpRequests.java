package lk.tech.tgcontrollerclient.services;

import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import lk.tech.tgcontrollerclient.dto.ResultString;
import lk.tech.tgcontrollerclient.utils.BaseProvider;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class HttpRequests {

    private final ObjectMapper mapper = new ObjectMapper();
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

    // ---------------- Object ----------------
    public Mono<Void> sendObject(String key, String command, ResultString result) {

        System.out.println("Sending object");

        String json;
        try {
            json = mapper.writeValueAsString(result);
        } catch (Exception e) {
            return Mono.error(e);
        }

        return client
                .headers(h -> h.add("Content-Type", "application/json"))
                .post()
                .uri("/api/v1/answer/object/" + key + "?command=" + command + "&status=" + result.getStatus())
                .send(Mono.just(json)
                        .map(s -> Unpooled.wrappedBuffer(s.getBytes(StandardCharsets.UTF_8)))
                )
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
