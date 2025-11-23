package lk.tech.tgcontrollerclient.ui;

import lk.tech.tgcontrollerclient.Main;
import lk.tech.tgcontrollerclient.utils.KeyManager;

import java.awt.*;
import java.io.Closeable;
import java.util.concurrent.CountDownLatch;

public class UI {

    private final Closeable client;
    private final CountDownLatch wait = new CountDownLatch(1);
    private TrayIcon trayIcon;

    public UI(Closeable client) {
        this.client = client;
    }

    /**
     * Настройка иконки в системном трее
     */
    public void setupTrayIcon() throws AWTException {
        if (!SystemTray.isSupported()) {
            throw new RuntimeException("System tray not supported!");
        }

        SystemTray tray = SystemTray.getSystemTray();

        // загружаем иконку
        Image image = Toolkit.getDefaultToolkit().createImage(
                Main.class.getResource("/icon.png")
        );

        PopupMenu menu = new PopupMenu();
        addRegenerateKeyItem(menu);
        addExitMenuItem(menu, tray);

        trayIcon = new TrayIcon(image, "Desktop Control Telegram", menu);
        trayIcon.setImageAutoSize(true);
        tray.add(trayIcon);
    }

    /**
     * Кнопка перегенерации ключа
     */
    private void addRegenerateKeyItem(PopupMenu menu) {
        MenuItem regenItem = new MenuItem("Regenerate Key");

        regenItem.addActionListener(e -> {
            try {
                String newKey = KeyManager.regenerateKey();
                trayIcon.displayMessage(
                        "Desktop Control Telegram",
                        "Новый ключ сгенерирован:\n" + newKey,
                        TrayIcon.MessageType.INFO
                );
                System.out.println("Новый ключ: " + newKey);
            } catch (Exception ex) {
                trayIcon.displayMessage(
                        "Error",
                        "Не удалось перегенерировать ключ",
                        TrayIcon.MessageType.ERROR
                );
            }
        });

        menu.add(regenItem);
    }

    /**
     * Добавление кнопки Exit в меню TrayIcon
     */
    private void addExitMenuItem(PopupMenu menu, SystemTray tray) {
        MenuItem exitItem = new MenuItem("Exit");

        exitItem.addActionListener(e -> {
            System.out.println("Shutting down...");
            shutdown(tray);
        });

        menu.add(exitItem);
    }

    /**
     * Корректное завершение программы
     */
    private void shutdown(SystemTray tray) {
        try {
            if (client != null) {
                client.close();
            }
            if (trayIcon != null) {
                tray.remove(trayIcon);
            }
        } catch (Exception ignored) {
        }

        wait.countDown(); // разблокирует start()
        System.exit(0);
    }

    public void await() throws InterruptedException {
        wait.await();
    }
}
