package lk.tech.tgcontrollerclient.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

@Slf4j
public class BaseProvider {

    private static final String socketUrl;
    private static final String httpUrl;

    static {
        try {
            Properties props = loadInternalProperties();

            socketUrl = props.getProperty("socketUrl");
            httpUrl = props.getProperty("httpUrl");
        } catch (Exception e) {
            throw new RuntimeException("Ошибка инициализации BaseProvider", e);
        }
    }

    // ------------ PUBLIC GETTERS ------------

    public static String key() {
        String key = KeyManager.INSTANCE.key();
        log.info(key);
        return key;
    }

    public static String socketUrl() {
        return socketUrl;
    }

    public static String httpUrl() {
        return httpUrl;
    }


    // ------------ INTERNAL LOGIC ------------

    /** Читаем config.properties прямо из JAR (src/main/resources) */
    private static Properties loadInternalProperties() throws IOException {
        Properties props = new Properties();

        try (InputStream is = BaseProvider.class.getResourceAsStream("/config.properties")) {
            if (is == null) {
                throw new FileNotFoundException(
                        "config.properties не найден в resources внутри JAR");
            }
            props.load(new InputStreamReader(is, StandardCharsets.UTF_8));
        }

        return props;
    }


}
