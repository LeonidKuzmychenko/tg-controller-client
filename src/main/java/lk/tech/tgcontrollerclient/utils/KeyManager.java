package lk.tech.tgcontrollerclient.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.UUID;

public class KeyManager {

    private static String key;

    private static final Path CONFIG_DIR =
            Paths.get(System.getenv("APPDATA"), "Desktop Control Telegram");
    private static final Path KEY_FILE = CONFIG_DIR.resolve("client.key");

    static {
        try {
            Files.createDirectories(CONFIG_DIR);
            key = loadOrGenerateKey();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка инициализации KeyManager", e);
        }
    }

    public static String key() {
        return key;
    }

    /** Генерация нового ключа + сохранение на диск */
    public static String regenerateKey() {
        try {
            String newKey = generateKey();
            Files.writeString(KEY_FILE, newKey, StandardCharsets.UTF_8);
            key = newKey;
            return newKey;
        } catch (IOException e) {
            throw new RuntimeException("Не удалось перегенерировать ключ", e);
        }
    }

    /** Чтение существующего или создание нового ключа */
    private static String loadOrGenerateKey() throws IOException {
        if (Files.exists(KEY_FILE)) {
            String existing = Files.readString(KEY_FILE, StandardCharsets.UTF_8).trim();
            if (!existing.isEmpty()) {
                return existing;
            }
        }
        // если файла нет или пустой → генерируем новый
        return regenerateKey();
    }

    private static String generateKey() {
        return "CLIENT_" + UUID.randomUUID();
    }
}
