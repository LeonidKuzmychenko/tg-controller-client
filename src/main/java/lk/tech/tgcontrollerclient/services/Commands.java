package lk.tech.tgcontrollerclient.services;

import lk.tech.tgcontrollerclient.dto.Result;
import lk.tech.tgcontrollerclient.dto.ResultImages;
import lk.tech.tgcontrollerclient.dto.ResultString;
import lk.tech.tgcontrollerclient.services.commands.AbstractCommand;
import lk.tech.tgcontrollerclient.services.commands.impl.*;
import lk.tech.tgcontrollerclient.utils.BaseProvider;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

public enum Commands {

    INSTANCE;  // <--- Singleton

    private final AbstractCommand firstCommand;

    Commands() {
        List<AbstractCommand> commands = new ArrayList<>();
        commands.add(new CommandInfo());
        commands.add(new CommandIP());
        commands.add(new CommandPcLoad());
        commands.add(new CommandProcesses());
        commands.add(new CommandScreenshot());
        commands.add(new CommandShutdown());
        commands.add(new CommandSpeedTest());
        commands.add(new CommandTemp());

        // связываем цепочку
        for (int i = 0; i < commands.size() - 1; i++) {
            commands.get(i).setNext(commands.get(i + 1));
        }

        this.firstCommand = commands.getFirst();
    }

    public void analyze(String command) {
        Result result = firstCommand.analyze(command);
        System.out.println("Result: " + result);

        switch (result) {
            case ResultString res -> HttpRequests.INSTANCE
                    .sendObject(BaseProvider.key(), command, res).block();

            case ResultImages res ->
                    Flux.fromIterable(res.getImages())
                            .subscribe(image -> HttpRequests.INSTANCE
                                    .sendImage(image, BaseProvider.key(), command, result.getStatus()).block());

            case Result res -> HttpRequests.INSTANCE
                    .sendText(BaseProvider.key(), command, res.getStatus()).block();
        }
    }
}
