package lk.tech.tgcontrollerclient.services.commands.impl;

import lk.tech.tgcontrollerclient.dto.Result;
import lk.tech.tgcontrollerclient.dto.ResultString;
import lk.tech.tgcontrollerclient.services.commands.AbstractCommand;

import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.util.Scanner;

public class CommandIP extends AbstractCommand {

    @Override
    public Result run(String command) {

        try {
            String local = InetAddress.getLocalHost().getHostAddress();

            String external;
            try (Scanner s = new Scanner(URI.create("https://api.ipify.org").toURL().openStream())) {
                external = s.nextLine();
            }

            String data = """
                    Локальный IP: %s
                    Внешний IP: %s
                    """.formatted(local, external);

            return new ResultString("Success", data);

        } catch (Exception e) {
            return new Result("Fail");
        }
    }

    @Override
    public String condition() {
        return "/ip";
    }
}
