package lk.tech.tgcontrollerclient.ui;

import com.formdev.flatlaf.FlatDarkLaf;
import lk.tech.tgcontrollerclient.Main;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Closeable;
import java.net.URL;
import java.util.concurrent.CountDownLatch;

public class MyUI {
    private final Closeable client;
    private final CountDownLatch wait = new CountDownLatch(1);
    private final JFrame invokerFrame = new JFrame();

    // <<< ДОБАВЛЕНО: храним иконку, чтобы её удалить позже
    private TrayIcon trayIcon;

    public MyUI(Closeable client) {
        this.client = client;

        invokerFrame.setUndecorated(true);
        invokerFrame.setOpacity(0f);
        invokerFrame.setType(Window.Type.UTILITY);
        invokerFrame.setAlwaysOnTop(true);
        invokerFrame.setSize(1, 1);
    }

    public void setupTrayIcon() throws AWTException {
        FlatDarkLaf.setup();

        UIManager.put("MenuItem.iconTextGap", 4);
        UIManager.put("MenuItem.checkIcon", null);
        UIManager.put("MenuItem.arrowIcon", null);
        UIManager.put("MenuItem.icon", null);
        UIManager.put("MenuItem.minimumIconSize", new Dimension(0, 0));

        UIManager.put("PopupMenu.border", BorderFactory.createEmptyBorder(4, 4, 4, 4));
        UIManager.put("PopupMenu.background", new Color(40, 40, 40));
        UIManager.put("MenuItem.background", new Color(40, 40, 40));
        UIManager.put("MenuItem.selectionBackground", new Color(70, 70, 75));
        UIManager.put("MenuItem.selectionForeground", Color.WHITE);
        UIManager.put("MenuItem.foreground", Color.WHITE);
        UIManager.put("Separator.foreground", new Color(90, 90, 90));

        if (!SystemTray.isSupported()) return;

        URL iconUrl = Main.class.getResource("/icon.png");
        Image image = Toolkit.getDefaultToolkit().createImage(iconUrl);

        // <<< trayIcon сохраняем в поле
        trayIcon = new TrayIcon(image, "Desktop Telegram Controller");
        trayIcon.setImageAutoSize(true);

        trayIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) showPopupMenu();
            }
        });

        SystemTray.getSystemTray().add(trayIcon);
    }



    private void showPopupMenu() {
        MenuSelectionManager.defaultManager().clearSelectedPath();

        JPopupMenu menu = new JPopupMenu();

        JMenuItem regen = new JMenuItem("Regenerate key");
        regen.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        regen.addActionListener(a -> System.out.println("Regenerate key"));
        menu.add(regen);

        menu.addSeparator();

        JMenuItem exit = new JMenuItem("Exit");
        exit.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        exit.addActionListener(a -> shutdown());
        menu.add(exit);

        Point mouse = MouseInfo.getPointerInfo().getLocation();
        menu.validate();
        Dimension size = menu.getPreferredSize();

        int frameX = mouse.x;
        int frameY = mouse.y - size.height;

        invokerFrame.setLocation(frameX, frameY);
        invokerFrame.setVisible(true);

        SwingUtilities.invokeLater(() -> menu.show(invokerFrame, 0, 0));

        menu.addPopupMenuListener(new PopupMenuListener() {
            @Override public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}
            @Override public void popupMenuCanceled(PopupMenuEvent e) {
                invokerFrame.setVisible(false);
            }
            @Override public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                invokerFrame.setVisible(false);
            }
        });
    }


    // <<< ПОЛНОСТЬЮ ПЕРЕПИСАННЫЙ shutdown() >>>
    private void shutdown() {
        try {
            System.out.println("[Shutdown] Closing client...");
            if (client != null) client.close();
        } catch (Exception ignored) {}

        try {
            System.out.println("[Shutdown] Hiding tray icon...");
            if (trayIcon != null) {
                SystemTray.getSystemTray().remove(trayIcon);
                trayIcon = null;
            }
        } catch (Exception e) {
            System.out.println("[Shutdown] Failed to remove tray icon: " + e);
        }

        try {
            System.out.println("[Shutdown] Hiding invokerFrame...");
            invokerFrame.setVisible(false);
            invokerFrame.dispose();
        } catch (Exception ignored) {

        }

        System.out.println("[Shutdown] Exiting...");
        wait.countDown();
        System.exit(0);
    }


    public void await() throws InterruptedException {
        wait.await();
    }
}
