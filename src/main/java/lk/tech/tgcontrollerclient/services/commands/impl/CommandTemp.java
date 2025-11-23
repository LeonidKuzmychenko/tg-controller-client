package lk.tech.tgcontrollerclient.services.commands.impl;

import lk.tech.tgcontrollerclient.dto.Result;
import lk.tech.tgcontrollerclient.dto.ResultString;
import lk.tech.tgcontrollerclient.services.commands.AbstractCommand;
import oshi.SystemInfo;

public class CommandTemp extends AbstractCommand {

    private final SystemInfo si = new SystemInfo();

    @Override
    public Result run(String command) {
        try {
            var sensors = si.getHardware().getSensors();

            double cpuTemp = sensors.getCpuTemperature();

            int fanRpm = 0;
            int[] fans = sensors.getFanSpeeds();
            if (fans != null && fans.length > 0) {
                fanRpm = fans[0];
            }

            // В OSHI 6.9.1 максимум доступного — температура + кулеры
            String data = """
                    Температура CPU: %.1f°C
                    Обороты кулера: %d RPM
                    """.formatted(
                    cpuTemp,
                    fanRpm
            );

            return new ResultString("Success", data);

        } catch (Exception e) {
            return new Result("Fail");
        }
    }

    @Override
    public String condition() {
        return "/temp";
    }
}
