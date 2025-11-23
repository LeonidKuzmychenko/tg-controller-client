package lk.tech.tgcontrollerclient.commands.impl;

import lk.tech.tgcontrollerclient.commands.AbstractCommand;
import lk.tech.tgcontrollerclient.dto.Result;
import lk.tech.tgcontrollerclient.dto.ResultImages;
import lk.tech.tgcontrollerclient.services.ScreenCapture;

import java.util.List;

public class CommandScreenshot extends AbstractCommand {

    @Override
    public Result run(String command) {
        try {
            List<byte[]> listBytes = ScreenCapture.captureAllScreens();
            return new ResultImages("Success", listBytes);
        } catch (Exception e) {
            return new Result("Error");
        }
    }

    @Override
    public String condition() {
        return "/screenshot";
    }
}
