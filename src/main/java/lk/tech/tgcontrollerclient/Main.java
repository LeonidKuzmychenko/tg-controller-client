package lk.tech.tgcontrollerclient;

import lk.tech.tgcontrollerclient.socket.ReactorWsClient;

import java.awt.*;
import java.util.concurrent.CountDownLatch;

public class Main {

    private TrayIcon trayIcon;
    private ReactorWsClient client;
    private final CountDownLatch wait = new CountDownLatch(1);

    /**
     * Java 25 поддерживает нестатический main,
     * но статический всё ещё лучший вариант
     * для desktop/installer приложений.
     */
    public static void main(String[] args) throws Exception {
        new Main().start();
    }

    /**
     * Инициализация Desktop-клиента
     */
    private void start() throws Exception {
        client = new ReactorWsClient();
        client.safeConnect();

        setupTrayIcon();

        // удерживаем приложение живым
        wait.await();
    }

    /**
     * Настройка иконки в системном трее
     */
    private void setupTrayIcon() throws AWTException {
        if (!SystemTray.isSupported()) {
            throw new RuntimeException("System tray not supported!");
        }

        SystemTray tray = SystemTray.getSystemTray();

        // загружаем иконку
        Image image = Toolkit.getDefaultToolkit().createImage(
                Main.class.getResource("/icon.png")
        );

        PopupMenu menu = new PopupMenu();
        addExitMenuItem(menu, tray);

        trayIcon = new TrayIcon(image, "Desktop Control Telegram", menu);
        trayIcon.setImageAutoSize(true);
        tray.add(trayIcon);
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
}
