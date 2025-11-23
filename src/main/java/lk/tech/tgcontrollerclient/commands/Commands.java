package lk.tech.tgcontrollerclient.commands;

import lk.tech.tgcontrollerclient.BaseProvider;
import lk.tech.tgcontrollerclient.commands.impl.CommandScreenshot;
import lk.tech.tgcontrollerclient.commands.impl.CommandShutdown;
import lk.tech.tgcontrollerclient.commands.impl.CommandTimedShutdown;
import lk.tech.tgcontrollerclient.dto.Result;
import lk.tech.tgcontrollerclient.dto.ResultImages;
import lk.tech.tgcontrollerclient.web.HttpRequests;
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
            case ResultImages resultImages -> Flux.fromIterable(resultImages.getImages()).subscribe(image -> {
                requests.sendImage(image, BaseProvider.key(), command, result.getStatus()).block();
            });
            case Result base -> requests.sendText(BaseProvider.key(), command, base.getStatus()).block();
        }
    }
}
