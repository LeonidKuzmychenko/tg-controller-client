package lk.tech.tgcontrollerclient.ui;

import com.formdev.flatlaf.FlatDarkLaf;
import lk.tech.tgcontrollerclient.Main;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Closeable;
import java.net.URL;

@Slf4j
@NoArgsConstructor
public enum TrayService implements Closeable {

    INSTANCE;

    private TrayIcon trayIcon;

    public void setupTrayIcon() throws AWTException {
        if (!SystemTray.isSupported()) return;

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

        // Загружаем иконку
        URL iconUrl = Main.class.getResource("/icon.png");
        Image image = Toolkit.getDefaultToolkit().createImage(iconUrl);

        trayIcon = new TrayIcon(image, "Desktop Telegram Controller");
        trayIcon.setImageAutoSize(true);

        trayIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    MenuService.INSTANCE.showPopupMenu();
                }
            }
        });

        SystemTray.getSystemTray().add(trayIcon);
    }

    @Override
    public void close() {
        try {
            log.info("[Shutdown] Hiding tray icon...");
            if (trayIcon != null) {
                SystemTray.getSystemTray().remove(trayIcon);
                trayIcon = null;
            }
        } catch (Exception e) {
            log.info("[Shutdown] Failed to remove tray icon: " + e);
        }
    }
}
