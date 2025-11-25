package lk.tech.tgcontrollerclient.services;

import lk.tech.tgcontrollerclient.dto.Result;
import lk.tech.tgcontrollerclient.dto.ResultImages;
import lk.tech.tgcontrollerclient.dto.ResultString;
import lk.tech.tgcontrollerclient.services.commands.AbstractCommand;
import lk.tech.tgcontrollerclient.services.commands.impl.*;
import lk.tech.tgcontrollerclient.utils.BaseProvider;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public enum Commands {

    INSTANCE;  // <--- Singleton

    private final AbstractCommand firstCommand;

    Commands() {
        List<AbstractCommand> commands = new ArrayList<>();
        commands.add(new CommandIP());
        commands.add(new CommandScreenshot());
        commands.add(new CommandShutdown());

        // связываем цепочку
        for (int i = 0; i < commands.size() - 1; i++) {
            commands.get(i).setNext(commands.get(i + 1));
        }

        this.firstCommand = commands.getFirst();
    }

    public void analyze(String command) {
        Result result = firstCommand.analyze(command);
        log.info("Result: {}", result);

        switch (result) {
            case ResultString res -> HttpRequests.INSTANCE.sendObject(BaseProvider.key(), command, res);
            case ResultImages res -> {
                for (byte[] image : res.getImages()) {
                    HttpRequests.INSTANCE.sendImage(image, BaseProvider.key(), command, result.getStatus());
                }
            }
            case Result res -> HttpRequests.INSTANCE.sendText(BaseProvider.key(), command, res.getStatus());
        }
    }
}
