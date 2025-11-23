package lk.tech.tgcontrollerclient.services.commands.impl;

import lk.tech.tgcontrollerclient.dto.Result;
import lk.tech.tgcontrollerclient.dto.ResultString;
import lk.tech.tgcontrollerclient.services.commands.AbstractCommand;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.NetworkIF;

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

        String down = formatBytes(downBytes * 3); // *3 т.к. 300ms → 1 sec
        String up = formatBytes(upBytes * 3);

        // === GPU (если есть) ===
        String gpuLoad = "N/A";
        try {
            var gpus = systemInfo.getHardware().getGraphicsCards();
            if (!gpus.isEmpty()) {
                // GPU загрузку OSHI напрямую не даёт → пишем только имя/память
                gpuLoad = gpus.get(0).getName();
            }
        } catch (Exception ignored) {}

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

    private String formatBytes(long bytesPerSec) {
        double kb = bytesPerSec / 1024.0;
        double mb = kb / 1024.0;

        if (mb > 1) return String.format("%.1f MB", mb);
        if (kb > 1) return String.format("%.0f KB", kb);
        return bytesPerSec + " B";
    }
}
