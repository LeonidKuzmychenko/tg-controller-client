package lk.tech.tgcontrollerclient.ui;

import lk.tech.tgcontrollerclient.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Closeable;
import java.util.concurrent.CountDownLatch;

public class UI {

    private final Closeable client;
    private final CountDownLatch wait = new CountDownLatch(1);
    private TrayIcon trayIcon;

    public UI(Closeable client) {
        this.client = client;
    }

    public void setupTrayIcon() throws AWTException {
        System.out.println("[UI] setupTrayIcon() called");

        if (!SystemTray.isSupported()) {
            throw new RuntimeException("System tray not supported!");
        }

        SystemTray tray = SystemTray.getSystemTray();

        var url = Main.class.getResource("/icon.png");
        System.out.println("[UI] icon resource = " + url);
        if (url == null) {
            throw new RuntimeException("icon.png not found in resources!");
        }

        Image image = Toolkit.getDefaultToolkit().createImage(url);

        trayIcon = new TrayIcon(image, "Desktop Control Telegram");
        trayIcon.setImageAutoSize(true);

        // обработчик кликов
        trayIcon.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    System.out.println("[Tray] Right click at: " + e.getLocationOnScreen());
                    showCustomMenu(e);
                }
            }
        });

        tray.add(trayIcon);
        System.out.println("[UI] Tray icon added");
    }
    private void showCustomMenu(MouseEvent e) {
        SwingUtilities.invokeLater(() -> {
            // Берём реальную позицию мыши
            PointerInfo pi = MouseInfo.getPointerInfo();
            Point mouse = pi.getLocation();

            // Размеры меню
            int menuWidth = 220;
            int menuHeight = 160;

            // Левый нижний угол меню = точка мыши
            int x = mouse.x;
            int y = mouse.y - menuHeight;

            // Корректировка по экрану
            Rectangle screen = GraphicsEnvironment.getLocalGraphicsEnvironment()
                    .getDefaultScreenDevice()
                    .getDefaultConfiguration()
                    .getBounds();

            if (x + menuWidth > screen.x + screen.width)
                x = screen.x + screen.width - menuWidth - 5;

            if (y < screen.y)
                y = screen.y + 5;

            Point finalPoint = new Point(x, y);

            Windows11Menu menu = new Windows11Menu(finalPoint, trayIcon, client, wait);
            menu.showMenu();
        });
    }


    public void await() throws InterruptedException {
        wait.await();
    }
}
