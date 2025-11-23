package lk.tech.tgcontrollerclient.services.commands;

import lk.tech.tgcontrollerclient.dto.Result;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class AbstractCommand {

    private AbstractCommand next;

    public Result analyze(String command) {
        if (command.equals(condition())) {
            return run(command);
        }
        AbstractCommand next = getNext();
        if (next != null) {
            return next.analyze(command);
        }
        return new Result("Unknown");
    }


    public abstract Result run(String command);

    protected abstract String condition();
}
