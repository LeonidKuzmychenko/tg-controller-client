package lk.tech.tgcontrollerclient.ui;

import lombok.NoArgsConstructor;

import java.awt.*;
import java.io.Closeable;
import java.util.concurrent.CountDownLatch;

@NoArgsConstructor
public enum MyUI implements Closeable {

    INSTANCE;

    private final CountDownLatch wait = new CountDownLatch(1);

    public void start() throws AWTException {
        TrayService.INSTANCE.setupTrayIcon();
    }

    public void await() throws InterruptedException {
        wait.await();
    }

    @Override
    public void close() {
        wait.countDown();
        System.exit(0);
    }
}
