package lk.tech.tgcontrollerclient.ui;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import lk.tech.tgcontrollerclient.Main;
import lk.tech.tgcontrollerclient.utils.KeyManager;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Closeable;
import java.util.concurrent.CountDownLatch;

public class UiNew {

    private final Closeable client;
    private final CountDownLatch wait = new CountDownLatch(1);
    private TrayIcon trayIcon;
    private final JFrame invokerFrame = new JFrame();

    public UiNew(Closeable client) {
        this.client = client;
        invokerFrame.setUndecorated(true);
        invokerFrame.setOpacity(0);
        invokerFrame.setType(Window.Type.UTILITY);
        invokerFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    public void setupTrayIcon() throws AWTException {

        try {
            System.setProperty("flatlaf.useWindowTheme", "true");
            FlatDarkLaf.setup();
            System.out.println("Тема установлена с автовыбором.");
        } catch (Exception ex) {
            System.err.println("Failed to initialize FlatLaf: " + ex.getMessage());
            FlatLightLaf.setup();
        }

        if (!SystemTray.isSupported()) {
            throw new RuntimeException("System tray not supported!");
        }

        SystemTray tray = SystemTray.getSystemTray();
        Image image = Toolkit.getDefaultToolkit().createImage(
                Main.class.getResource("/icon.png")
        );

        trayIcon = new TrayIcon(image, "Desktop Control Telegram");
        trayIcon.setImageAutoSize(true);

        trayIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger() || e.getButton() == MouseEvent.BUTTON1) {
                    showSwingMenu(tray);
                }
            }
        });

        tray.add(trayIcon);
    }

    private void showSwingMenu(SystemTray tray) {
        JPopupMenu swingMenu = new JPopupMenu();
        addRegenerateKeyItemSwing(swingMenu);
        swingMenu.addSeparator();
        addExitMenuItemSwing(swingMenu, tray);

        PointerInfo pointer = MouseInfo.getPointerInfo();
        Point screenPoint = pointer.getLocation();

        swingMenu.validate();
        Dimension menuSize = swingMenu.getPreferredSize();

        int frameX = (int) (screenPoint.getX() - menuSize.width);
        int frameY = (int) (screenPoint.getY() - menuSize.height - 2);

        invokerFrame.setLocation(frameX, frameY);
        invokerFrame.setSize(1, 1);
        if (!invokerFrame.isVisible()) {
            invokerFrame.setVisible(true);
        }

        swingMenu.show(invokerFrame, 0, 0);

        swingMenu.addPopupMenuListener(new PopupMenuListener() {
            @Override public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}
            @Override public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                invokerFrame.setVisible(false);
            }
            @Override public void popupMenuCanceled(PopupMenuEvent e) {
                invokerFrame.setVisible(false);
            }
        });
    }

    private void addRegenerateKeyItemSwing(JPopupMenu menu) {
        JMenuItem regenItem = new JMenuItem("Regenerate Key");
        regenItem.addActionListener(e -> {
            String newKey = KeyManager.regenerateKey();
            System.out.println("Новый ключ: " + newKey);
        });
        menu.add(regenItem);
    }

    private void addExitMenuItemSwing(JPopupMenu menu, SystemTray tray) {
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> {
            System.out.println("Shutting down...");
            shutdown(tray);
        });
        menu.add(exitItem);
    }

    private void shutdown(SystemTray tray) {
        try {
            if (client != null) {
                client.close();
            }
            if (trayIcon != null) {
                tray.remove(trayIcon);
            }
            invokerFrame.dispose();
        } catch (Exception ignored) {
        }
        wait.countDown();
        System.exit(0);
    }

    public void await() throws InterruptedException {
        wait.await();
    }
}