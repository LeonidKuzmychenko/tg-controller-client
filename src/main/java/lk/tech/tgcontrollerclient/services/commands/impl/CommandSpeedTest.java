package lk.tech.tgcontrollerclient.services.commands.impl;

import lk.tech.tgcontrollerclient.dto.Result;
import lk.tech.tgcontrollerclient.dto.ResultString;
import lk.tech.tgcontrollerclient.services.commands.AbstractCommand;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;

public class CommandSpeedTest extends AbstractCommand {

    @Override
    public Result run(String command) {
        try {
            long startPing = System.currentTimeMillis();

            URI.create("https://api.ipify.org").toURL().openConnection().connect();
            long ping = System.currentTimeMillis() - startPing;

            // тест скорости скачивания (примерно 1MB)
            URL url = URI.create("https://speed.hetzner.de/1MB.bin").toURL();
            byte[] buffer = new byte[8192];

            long start = System.currentTimeMillis();
            int total = 0;

            try (InputStream is = url.openStream()) {
                int read;
                while ((read = is.read(buffer)) > 0 && total < 1_000_000) {
                    total += read;
                }
            }

            double seconds = (System.currentTimeMillis() - start) / 1000.0;
            double mbps = (total / 1024.0 / 1024) / seconds;

            String result = """
                    Пинг: %d ms
                    Скорость загрузки: %.2f MB/s
                    """.formatted(ping, mbps);

            return new ResultString("Success", result);

        } catch (Exception e) {
            return new Result("Fail");
        }
    }

    @Override
    public String condition() {
        return "/speedtest";
    }
}
