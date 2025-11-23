package lk.tech.tgcontrollerclient.ui;

import com.formdev.flatlaf.FlatLightLaf; // Импорт FlatLaf
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

public class UI {

    private final Closeable client;
    private final CountDownLatch wait = new CountDownLatch(1);
    private TrayIcon trayIcon;

    // Используем одну невидимую рамку (invokerFrame) для всех операций Swing, связанных с треем.
    private final JFrame invokerFrame = new JFrame();

    public UI(Closeable client) {
        this.client = client;

        // Настройка невидимой рамки (invokerFrame) один раз в конструкторе
        invokerFrame.setUndecorated(true);
        invokerFrame.setOpacity(0); // Делаем полностью невидимым
        invokerFrame.setType(Window.Type.UTILITY);
        invokerFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    /**
     * Настройка иконки в системном трее
     */
    public void setupTrayIcon() throws AWTException {
        // *** 🔑 Активация FlatLaf для современного вида ***
        try {
            // Используем светлую тему FlatLaf. Можно использовать FlatDarkLaf для темной.
            FlatLightLaf.setup();
        } catch (Exception ex) {
            System.err.println("Failed to initialize FlatLaf: " + ex.getMessage());
        }

        if (!SystemTray.isSupported()) {
            throw new RuntimeException("System tray not supported!");
        }

        SystemTray tray = SystemTray.getSystemTray();

        // Загружаем иконку
        Image image = Toolkit.getDefaultToolkit().createImage(
                Main.class.getResource("/icon.png")
        );

        // TrayIcon создается БЕЗ AWT PopupMenu
        trayIcon = new TrayIcon(image, "Desktop Control Telegram");
        trayIcon.setImageAutoSize(true);

        // Добавляем слушателя для ручного отображения Swing-меню
        trayIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                // Условие для Windows/Linux (правая кнопка)
                if (e.isPopupTrigger()) {
                    showSwingMenu(tray);
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                // Условие для macOS (обычно левая кнопка)
                if (e.getButton() == MouseEvent.BUTTON1) {
                    showSwingMenu(tray);
                }
            }
        });

        tray.add(trayIcon);
    }

    /**
     * Создание и отображение красивого Swing-меню с корректной позицией
     */
    private void showSwingMenu(SystemTray tray) {
        // Мы уже установили FlatLaf, поэтому системный L&F здесь не нужен.

        // 1. Создаем меню
        JPopupMenu swingMenu = new JPopupMenu();
        addRegenerateKeyItemSwing(swingMenu);
        swingMenu.addSeparator();
        addExitMenuItemSwing(swingMenu, tray);

        // 2. Получаем точные экранные координаты курсора
        PointerInfo pointer = MouseInfo.getPointerInfo();
        Point screenPoint = pointer.getLocation();

        // Получаем размеры меню (важно вызвать validate/getPreferredSize после добавления элементов)
        swingMenu.validate();
        Dimension menuSize = swingMenu.getPreferredSize();

        // 3. Расчет позиции для invokerFrame (чтобы меню открылось вверх-влево от курсора)
        int frameX = (int) (screenPoint.getX() - menuSize.width);
        int frameY = (int) (screenPoint.getY() - menuSize.height - 2); // -2 для небольшого отступа

        // 4. Позиционируем невидимый invokerFrame
        invokerFrame.setLocation(frameX, frameY);
        invokerFrame.setSize(1, 1);

        // Отображаем invokerFrame
        if (!invokerFrame.isVisible()) {
            invokerFrame.setVisible(true);
        }

        // 5. Отображаем меню относительно invokerFrame в позиции (0, 0)
        swingMenu.show(invokerFrame, 0, 0);

        // 6. Закрытие фрейма после потери фокуса меню
        swingMenu.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}
            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                invokerFrame.setVisible(false); // Скрываем
            }
            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
                invokerFrame.setVisible(false);
            }
        });
    }

    /**
     * Кнопка перегенерации ключа (Swing-версия)
     */
    private void addRegenerateKeyItemSwing(JPopupMenu menu) {
        JMenuItem regenItem = new JMenuItem("Regenerate Key");

        regenItem.addActionListener(e -> {
            try {
                String newKey = KeyManager.regenerateKey();
                // AWT-сообщения нужно вызывать в потоке EDT или убедиться, что они безопасны.
                // SwingUtilities.invokeLater гарантирует безопасность потоков при вызове UI.
                SwingUtilities.invokeLater(() -> {
                    trayIcon.displayMessage(
                            "Desktop Control Telegram",
                            "Новый ключ сгенерирован:\n" + newKey,
                            TrayIcon.MessageType.INFO
                    );
                });
                System.out.println("Новый ключ: " + newKey);
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    trayIcon.displayMessage(
                            "Error",
                            "Не удалось перегенерировать ключ",
                            TrayIcon.MessageType.ERROR
                    );
                });
            }
        });

        menu.add(regenItem);
    }

    /**
     * Добавление кнопки Exit в меню TrayIcon (Swing-версия)
     */
    private void addExitMenuItemSwing(JPopupMenu menu, SystemTray tray) {
        JMenuItem exitItem = new JMenuItem("Exit");

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
            // Удаляем invokerFrame при завершении работы
            invokerFrame.dispose();
        } catch (Exception ignored) {
        }

        wait.countDown(); // разблокирует start()
        System.exit(0);
    }

    public void await() throws InterruptedException {
        wait.await();
    }
}