package lk.tech.tgcontrollerclient.services;

import lk.tech.tgcontrollerclient.dto.ResultString;
import lk.tech.tgcontrollerclient.services.commands.impl.CommandPcLoad;
import lk.tech.tgcontrollerclient.utils.BaseProvider;
import lk.tech.tgcontrollerclient.services.commands.AbstractCommand;
import lk.tech.tgcontrollerclient.services.commands.impl.CommandScreenshot;
import lk.tech.tgcontrollerclient.services.commands.impl.CommandShutdown;
import lk.tech.tgcontrollerclient.services.commands.impl.CommandTimedShutdown;
import lk.tech.tgcontrollerclient.dto.Result;
import lk.tech.tgcontrollerclient.dto.ResultImages;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

public class Commands {

    private final AbstractCommand firstCommand;
    private final HttpRequests requests;

    public Commands() {
        List<AbstractCommand> commands = new ArrayList<>();
        commands.add(new CommandShutdown());
        commands.add(new CommandTimedShutdown());
        commands.add(new CommandScreenshot());
        commands.add(new CommandPcLoad());
        for (int i = 0; i < commands.size() - 1; i++) {
            commands.get(i).setNext(commands.get(i + 1));
        }
        this.firstCommand = commands.getFirst();
        this.requests = new HttpRequests();
    }

    public void analyze(String command) {
        Result result = firstCommand.analyze(command);
        System.out.println("Result: " + result);
        switch (result) {
            case ResultString res -> requests.sendObject(BaseProvider.key(), command, res).block();
            case ResultImages res -> Flux.fromIterable(res.getImages())
                    .subscribe(image -> requests.sendImage(image, BaseProvider.key(), command, result.getStatus()).block());
            case Result res -> requests.sendText(BaseProvider.key(), command, res.getStatus()).block();
        }
    }
}
