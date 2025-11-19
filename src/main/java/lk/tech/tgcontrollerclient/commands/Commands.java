package lk.tech.tgcontrollerclient.commands;

import lk.tech.tgcontrollerclient.commands.impl.AbstractCommand;
import lk.tech.tgcontrollerclient.commands.impl.CommandScreenshot;
import lk.tech.tgcontrollerclient.commands.impl.CommandShutdown;
import lk.tech.tgcontrollerclient.commands.impl.CommandTimedShutdown;
import lk.tech.tgcontrollerclient.dto.Answer;

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

    public Answer analyze(String command) {
        System.out.println("Command: " + command);
        Answer answer = firstCommand.analyze(command);
        System.out.println("Answer: " + answer);
        return answer;
    }
}
