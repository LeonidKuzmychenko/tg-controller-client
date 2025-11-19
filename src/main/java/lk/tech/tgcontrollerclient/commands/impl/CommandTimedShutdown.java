package lk.tech.tgcontrollerclient.commands.impl;

import lk.tech.tgcontrollerclient.dto.Answer;

public class CommandTimedShutdown extends AbstractCommand {

    @Override
    public Answer run(String command) {
//        ProcessBuilder pb = new ProcessBuilder("shutdown", "/s", "/f", "/t", "0");
//        try {
//            pb.start();
//            return new Answer("/shutdown", null);
//        } catch (IOException e) {
//            return null;
//        }
        return new Answer(command, "Success");
    }

    @Override
    public String condition() {
        return "/shutdown_by_time";
    }
}
