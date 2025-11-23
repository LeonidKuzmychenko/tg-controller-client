package lk.tech.tgcontrollerclient.ui;

import lk.tech.tgcontrollerclient.utils.KeyManager;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Closeable;
import java.util.concurrent.CountDownLatch;

public class Windows11Menu extends JWindow {

    private final TrayIcon trayIcon;
    private final Closeable client;
    private final CountDownLatch wait;

    public Windows11Menu(Point location, TrayIcon trayIcon, Closeable client, CountDownLatch wait) {
        this.trayIcon = trayIcon;
        this.client = client;
        this.wait = wait;

        System.out.println("[Menu] Creating menu at " + location);

        setAlwaysOnTop(true);
        setFocusableWindowState(true);
        setType(Window.Type.POPUP);
        setBackground(new Color(0, 0, 0, 0)); // прозрачный фон JWindow

        JPanel panel = createPanel();

        add(panel);
        pack();

        setLocation(adjustToScreen(location));
        addGlobalHideListener();
    }

    /** Показываем меню */
    public void showMenu() {
        SwingUtilities.invokeLater(() -> {
            setVisible(true);
            toFront();
            requestFocus();
        });
    }

    /** Создаём красивую панель */
    private JPanel createPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1));
        panel.setBackground(new Color(245, 245, 245, 230));
        panel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true));
        panel.setPreferredSize(new Dimension(220, 160));
        panel.setOpaque(true);

        addItem(panel, "🔄 Regenerate key", () -> {
            String key = KeyManager.regenerateKey();
            trayIcon.displayMessage("Desktop Control Telegram",
                    "Новый ключ:\n" + key,
                    TrayIcon.MessageType.INFO);
        });

        addItem(panel, "📋 Copy key", () -> {
            StringSelection selection = new StringSelection(KeyManager.key());
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
        });

        panel.add(new JSeparator());

        addItem(panel, "❌ Exit", this::exitApp);

        return panel;
    }

    /** Добавление одного пункта меню */
    private void addItem(JPanel panel, String text, Runnable action) {
        JLabel item = new JLabel(text);
        item.setOpaque(true);
        item.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        item.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        item.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                item.setBackground(new Color(230, 230, 230));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                item.setBackground(new Color(0, 0, 0, 0));
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                action.run();
                setVisible(false);
            }
        });

        panel.add(item);
    }

    /** Если клик вне — скрываем меню */
    private void addGlobalHideListener() {
        Toolkit.getDefaultToolkit().addAWTEventListener(event -> {
            if (event instanceof MouseEvent me && me.getID() == MouseEvent.MOUSE_PRESSED) {
                if (!getBounds().contains(me.getLocationOnScreen())) {
                    setVisible(false);
                }
            }
        }, AWTEvent.MOUSE_EVENT_MASK);
    }

    /** Корректный выход */
    private void exitApp() {
        try {
            if (client != null) client.close();
        } catch (Exception ignored) { }

        wait.countDown();
        System.exit(0);
    }

    /** Корректное позиционирование, чтобы меню не улетало за экран */
    private Point adjustToScreen(Point p) {
        Rectangle screen = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getDefaultScreenDevice()
                .getDefaultConfiguration()
                .getBounds();

        int x = Math.min(p.x, screen.x + screen.width - 250);
        int y = Math.min(p.y, screen.y + screen.height - 200);

        return new Point(x, y);
    }
}
