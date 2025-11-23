package lk.tech.tgcontrollerclient.services.commands.impl;

import lk.tech.tgcontrollerclient.dto.Result;
import lk.tech.tgcontrollerclient.dto.ResultString;
import lk.tech.tgcontrollerclient.services.commands.AbstractCommand;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.NetworkIF;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CommandPcLoad extends AbstractCommand {

    private final SystemInfo systemInfo = new SystemInfo();

    @Override
    public Result run(String command) {
        try {
            String load = getSystemLoad();
            return new ResultString("Success", load);
        } catch (Exception e) {
            return new Result("Fail");
        }
    }

    @Override
    public String condition() {
        return "/load";
    }

    private String getSystemLoad() throws Exception {

        // === CPU ===
        CentralProcessor cpu = systemInfo.getHardware().getProcessor();
        long[] prevTicks = cpu.getSystemCpuLoadTicks();
        TimeUnit.MILLISECONDS.sleep(300);
        long cpuLoad = Math.round(cpu.getSystemCpuLoadBetweenTicks(prevTicks) * 100);

        // === RAM ===
        GlobalMemory mem = systemInfo.getHardware().getMemory();
        long total = mem.getTotal();
        long used = total - mem.getAvailable();
        long ramPercent = used * 100 / total;

        double totalGb = total / 1024.0 / 1024 / 1024;
        double usedGb = used / 1024.0 / 1024 / 1024;

        // === NETWORK ===
        List<NetworkIF> nets = systemInfo.getHardware().getNetworkIFs();
        long rx1 = 0, tx1 = 0;
        for (NetworkIF net : nets) {
            net.updateAttributes();
            rx1 += net.getBytesRecv();
            tx1 += net.getBytesSent();
        }

        TimeUnit.MILLISECONDS.sleep(300);

        long rx2 = 0, tx2 = 0;
        for (NetworkIF net : nets) {
            net.updateAttributes();
            rx2 += net.getBytesRecv();
            tx2 += net.getBytesSent();
        }

        long downBytes = rx2 - rx1;
        long upBytes = tx2 - tx1;

        String down = formatBytes(downBytes * 3); // 300 ms -> 1 sec
        String up = formatBytes(upBytes * 3);

        // === GPU LOAD via WMI ===
        String gpuLoad = getGpuLoadWindows();

        return """
                CPU: %d%%
                RAM: %.1f / %.1f GB (%d%%)
                GPU: %s
                NET: ↑ %s/s   ↓ %s/s
                """.formatted(
                cpuLoad,
                usedGb, totalGb, ramPercent,
                gpuLoad,
                up, down
        );
    }

    private String getGpuLoadWindows() {

        // --- 1. NVIDIA? — пробуем nvidia-smi ---
        try {
            String nvidia = getGpuLoadNvidia();
            if (!"N/A".equals(nvidia)) {
                return nvidia;
            }
        } catch (Exception ignored) {}

        // --- 2. AMD / Intel / fallback — PowerShell ---
        try {
            String ps = getGpuLoadPowerShell();
            if (!"N/A".equals(ps)) {
                return ps;
            }
        } catch (Exception ignored) {}

        return "N/A";
    }

    private String getGpuLoadPowerShell() {
        try {
            String command = "Get-Counter -Counter \"\\GPU Engine(*)\\Utilization Percentage\" " +
                            "| Select-Object -ExpandProperty CounterSamples " +
                            "| Select-Object -ExpandProperty CookedValue";

            ProcessBuilder pb = new ProcessBuilder(
                    "powershell",
                    "-Command",
                    command
            );

            pb.redirectErrorStream(true);
            Process process = pb.start();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))
            ) {
                List<Integer> values = reader.lines()
                        .filter(line -> line.matches("\\d+(\\.\\d+)?"))
                        .map(line -> (int) Math.round(Double.parseDouble(line)))
                        .toList();

                process.waitFor();

                if (!values.isEmpty()) {
                    int max = values.stream().max(Integer::compare).orElse(0);
                    return max + "%";
                }
            }

        } catch (Exception ignored) {}

        return "N/A";
    }

    private String getGpuLoadNvidia() {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "nvidia-smi",
                    "--query-gpu=utilization.gpu",
                    "--format=csv,noheader,nounits"
            );
            pb.redirectErrorStream(true);

            Process process = pb.start();

            try (BufferedReader r = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line = r.readLine();
                process.waitFor();

                if (line != null && line.trim().matches("\\d+")) {
                    return line.trim() + "%";
                }
            }
        } catch (Exception ignored) {}

        return "N/A";
    }

    private String formatBytes(long bytesPerSec) {
        double kb = bytesPerSec / 1024.0;
        double mb = kb / 1024.0;

        if (mb > 1) return String.format("%.1f MB", mb);
        if (kb > 1) return String.format("%.0f KB", kb);
        return bytesPerSec + " B";
    }
}
