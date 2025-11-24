package lk.tech.tgcontrollerclient.services.commands.impl;

import lk.tech.tgcontrollerclient.dto.Result;
import lk.tech.tgcontrollerclient.services.commands.AbstractCommand;

public class CommandShutdown extends AbstractCommand {

    @Override
    public Result run(String command) {
//        ProcessBuilder pb = new ProcessBuilder("shutdown", "/s", "/f", "/t", "0");
//        try {
//            pb.start();
//            return new Result("Success");
//        } catch (IOException e) {
//            return new Result("Fail");
//        }
        return new Result("Success");
    }

    @Override
    public String condition() {
        return "/shutdown";
    }
}
