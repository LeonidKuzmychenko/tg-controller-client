package lk.tech.tgcontrollerclient.services.commands.impl;

import lk.tech.tgcontrollerclient.dto.Result;
import lk.tech.tgcontrollerclient.dto.ResultString;
import lk.tech.tgcontrollerclient.services.commands.AbstractCommand;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CommandProcesses extends AbstractCommand {

    private final SystemInfo si = new SystemInfo();

    @Override
    public Result run(String command) {
        try {
            OperatingSystem os = si.getOperatingSystem();
            CentralProcessor cpu = si.getHardware().getProcessor();
            int logicalCpuCount = cpu.getLogicalProcessorCount();

            // Получаем список всех процессов
            List<OSProcess> processes = os.getProcesses();

            String list = processes.stream()
                    // сортируем по нашей вычисленной загрузке CPU
                    .sorted(Comparator.comparingDouble(
                            (OSProcess p) -> calcCpuPercent(p, logicalCpuCount)
                    ).reversed())
                    .limit(10)
                    .map(p -> {
                        double cpuPercent = calcCpuPercent(p, logicalCpuCount);
                        double ramMb = p.getResidentSetSize() / 1024.0 / 1024.0;

                        return "%s (PID %d) — %.1f%% CPU, %.1f MB RAM".formatted(
                                p.getName(),
                                p.getProcessID(),
                                cpuPercent,
                                ramMb
                        );
                    })
                    .collect(Collectors.joining("\n"));

            return new ResultString("Success", list);

        } catch (Exception e) {
            return new Result("Fail");
        }
    }

    @Override
    public String condition() {
        return "/processes";
    }

    /**
     * Приблизительный расчёт загрузки CPU процесса:
     * (kernelTime + userTime) / upTime / logicalCpuCount * 100
     */
    private double calcCpuPercent(OSProcess p, int logicalCpuCount) {
        long upTimeMs = p.getUpTime();          // сколько процесс живёт, мс
        long kernelMs = p.getKernelTime();      // время в kernel mode, мс
        long userMs = p.getUserTime();          // время в user mode, мс

        if (upTimeMs <= 0L || logicalCpuCount <= 0) {
            return 0.0;
        }

        double totalCpuTimeMs = kernelMs + userMs;
        double cpuLoad = totalCpuTimeMs / (upTimeMs * logicalCpuCount);

        return cpuLoad * 100.0;
    }
}
