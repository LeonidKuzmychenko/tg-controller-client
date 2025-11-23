package lk.tech.tgcontrollerclient.commands;

import lk.tech.tgcontrollerclient.commands.impl.CommandScreenshot;
import lk.tech.tgcontrollerclient.commands.impl.CommandShutdown;
import lk.tech.tgcontrollerclient.commands.impl.CommandTimedShutdown;
import lk.tech.tgcontrollerclient.dto.Result;

import java.util.ArrayList;
import java.util.List;

public class Commands {

    private final AbstractCommand firstCommand;

    public Commands() {
        List<AbstractCommand> commands = new ArrayList<>();
        commands.add(new CommandShutdown());
        commands.add(new CommandTimedShutdown());
        commands.add(new CommandScreenshot());
        for (int i = 0; i < commands.size() - 1; i++) {
            commands.get(i).setNext(commands.get(i + 1));
        }
        this.firstCommand = commands.getFirst();
    }

    public Result analyze(String command) {
        return firstCommand.analyze(command);
    }
}
