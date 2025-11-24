package lk.tech.tgcontrollerclient.utils;

import lombok.NoArgsConstructor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@NoArgsConstructor
public enum KeyManager {

    INSTANCE;

    private String key;
    private Path KEY_FILE;

    public void init() {
        try {
            Path CONFIG_DIR = Paths.get(System.getenv("APPDATA"), "Desktop Control Telegram");
            KEY_FILE = CONFIG_DIR.resolve("client.key");

            Files.createDirectories(CONFIG_DIR);
            key = loadOrGenerateKey();

        } catch (Exception e) {
            throw new RuntimeException("KeyManager init failed", e);
        }
    }

    public String key() {
        return key;
    }

    public String regenerateKey() {
        try {
            String newKey = generateKey();
            Files.writeString(KEY_FILE, newKey, StandardCharsets.UTF_8);
            key = newKey;
            return newKey;
        } catch (IOException e) {
            throw new RuntimeException("Unable to regenerate key", e);
        }
    }

    private String loadOrGenerateKey() throws IOException {
        if (Files.exists(KEY_FILE)) {
            String existing = Files.readString(KEY_FILE, StandardCharsets.UTF_8).trim();
            if (!existing.isEmpty()) return existing;
        }
        return regenerateKey();
    }

    private String generateKey() {
        return UUID.randomUUID().toString();
    }
}
