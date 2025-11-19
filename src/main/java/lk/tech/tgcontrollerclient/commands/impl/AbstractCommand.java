package lk.tech.tgcontrollerclient.commands.impl;

import lk.tech.tgcontrollerclient.dto.Answer;
import lombok.Getter;

@Getter
public abstract class AbstractCommand {

    private AbstractCommand next;

    public Answer analyze(String command) {
        if (command.equals(condition())) {
            return run(command);
        }
        AbstractCommand next = getNext();
        if (next != null) {
            return next.analyze(command);
        }
        return new Answer("/unknown", null);
    }


    abstract Answer run(String command);

    abstract String condition();

    public AbstractCommand setNext(AbstractCommand next) {
        this.next = next;
        return next;
    }
}
