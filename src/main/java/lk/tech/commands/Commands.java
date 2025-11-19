package lk.tech.commands;

import lk.tech.commands.impl.AbstractCommand;
import lk.tech.commands.impl.CommandScreenshot;
import lk.tech.commands.impl.CommandShutdown;
import lk.tech.commands.impl.CommandTimedShutdown;
import lk.tech.dto.Answer;

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
