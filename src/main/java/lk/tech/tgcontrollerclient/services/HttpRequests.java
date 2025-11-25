package lk.tech.tgcontrollerclient.services;

import lk.tech.tgcontrollerclient.dto.ResultString;
import lk.tech.tgcontrollerclient.utils.BaseProvider;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Slf4j
public enum HttpRequests {

    INSTANCE;

    private final OkHttpClient client;

    HttpRequests() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(Duration.ofSeconds(10))
                .writeTimeout(Duration.ofSeconds(10))
                .readTimeout(Duration.ofSeconds(10))
                .retryOnConnectionFailure(true)
                .build();
    }

    // ==================================================
    //                   SEND TEXT
    // ==================================================
    public void sendText(String key, String command, String status) {
        try {
            String url = BaseProvider.httpUrl() +
                    "/api/v1/answer/text/" + key +
                    "?command=" + encode(command) +
                    "&status=" + encode(status);

            Request request = new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(new byte[0]))
                    .build();

            client.newCall(request).enqueue(EMPTY_CALLBACK);

        } catch (Exception e) {
            log.error("[HTTP] sendText error", e);
        }
    }

    // ==================================================
    //                   SEND OBJECT
    // ==================================================
    public void sendObject(String key, String command, ResultString result) {
        try {
            String json = toJson(result);
            log.info("Sending object to server: {}", json);

            String url = BaseProvider.httpUrl() +
                    "/api/v1/answer/object/" + key +
                    "?command=" + encode(command) +
                    "&status=" + encode(result.getStatus());

            RequestBody body = RequestBody.create(
                    json.getBytes(StandardCharsets.UTF_8),
                    MediaType.parse("application/json")
            );

            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(EMPTY_CALLBACK);

        } catch (Exception e) {
            log.error("[HTTP] sendObject error", e);
        }
    }

    // ==================================================
    //                   SEND IMAGE (PNG)
    // ==================================================
    public void sendImage(byte[] image, String key, String command, String status) {
        try {
            String url = BaseProvider.httpUrl() +
                    "/api/v1/answer/image/" + key +
                    "?command=" + encode(command) +
                    "&status=" + encode(status);

            RequestBody body = RequestBody.create(
                    image,
                    MediaType.parse("image/png")
            );

            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(EMPTY_CALLBACK);

        } catch (Exception e) {
            log.error("[HTTP] sendImage error", e);
        }
    }

    // ==================================================
    //                     HELPERS
    // ==================================================

    private static String encode(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    // Минимальная ручная JSON сериализация
    private static String toJson(ResultString r) {
        return """
               {"status":"%s","data":"%s"}
               """.formatted(
                escape(r.getStatus()),
                escape(r.getData())
        );
    }

    private static String escape(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder(s.length());

        for (char c : s.toCharArray()) {
            switch (c) {
                case '\\' -> sb.append("\\\\");
                case '"'  -> sb.append("\\\"");
                case '\b' -> sb.append("\\b");
                case '\f' -> sb.append("\\f");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> {
                    if (c < 0x20) sb.append(String.format("\\u%04x", (int) c));
                    else sb.append(c);
                }
            }
        }

        return sb.toString();
    }

    // Пустой callback чтобы не спамить
    private static final Callback EMPTY_CALLBACK = new Callback() {
        @Override public void onFailure(Call call, IOException e) {
            log.error("[HTTP] Request failed: {}", e.getMessage());
        }

        @Override public void onResponse(Call call, Response response) {
            response.close();
        }
    };
}
