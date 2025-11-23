package lk.tech.tgcontrollerclient.services.commands.impl;

import lk.tech.tgcontrollerclient.dto.Result;
import lk.tech.tgcontrollerclient.dto.ResultString;
import lk.tech.tgcontrollerclient.services.commands.AbstractCommand;
import oshi.SystemInfo;

public class CommandInfo extends AbstractCommand {

    private final SystemInfo si = new SystemInfo();

    @Override
    public Result run(String command) {
        try {
            var hw = si.getHardware();
            var os = si.getOperatingSystem();

            String info = """
                    ОС: %s
                    CPU: %s
                    Ядер: %d
                    ОЗУ: %.1f GB
                    Видео: %s
                    """.formatted(
                    os.toString(),
                    hw.getProcessor().getProcessorIdentifier().getName(),
                    hw.getProcessor().getPhysicalProcessorCount(),
                    hw.getMemory().getTotal() / 1024.0 / 1024 / 1024,
                    hw.getGraphicsCards().isEmpty() ? "N/A" : hw.getGraphicsCards().getFirst().getName()
            );

            return new ResultString("Success", info);

        } catch (Exception e) {
            return new Result("Fail");
        }
    }

    @Override
    public String condition() {
        return "/info";
    }
}
