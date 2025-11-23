//package lk.tech.tgcontrollerclient.ui;
//
//import lk.tech.tgcontrollerclient.Main;
//import lk.tech.tgcontrollerclient.utils.KeyManager;
//
//import java.awt.*;
//import java.awt.event.ActionListener;
//import java.io.Closeable;
//import java.util.concurrent.CountDownLatch;
//
//// *** ВНИМАНИЕ: Здесь нет импорта FlatLaf, так как он не работает с AWT PopupMenu ***
//
//public class UI {
//
//    private final Closeable client;
//    private final CountDownLatch wait = new CountDownLatch(1);
//    private TrayIcon trayIcon;
//
//    // JFrame и вся сложная логика позиционирования удалены
//    // private final JFrame invokerFrame = new JFrame();
//
//    public UI(Closeable client) {
//        this.client = client;
//    }
//
//    /**
//     * Настройка иконки в системном трее с использованием нативного AWT PopupMenu
//     */
//    public void setupTrayIcon() throws AWTException {
//
//        // *** 🛑 FlatLaf.setup() здесь не нужен и не будет работать 🛑 ***
//
//        if (!SystemTray.isSupported()) {
//            throw new RuntimeException("System tray not supported!");
//        }
//
//        SystemTray tray = SystemTray.getSystemTray();
//
//        // Загружаем иконку
//        Image image = Toolkit.getDefaultToolkit().createImage(
//                Main.class.getResource("/icon.png")
//        );
//
//        // 1. Создание нативного AWT PopupMenu
//        PopupMenu popup = new PopupMenu();
//
//        // 2. Добавление пунктов меню
//        MenuItem regenItem = new MenuItem("Regenerate Key");
//        regenItem.addActionListener(createRegenKeyListener());
//        popup.add(regenItem);
//
//        popup.addSeparator();
//
//        MenuItem exitItem = new MenuItem("Exit");
//        exitItem.addActionListener(createExitListener(tray));
//        popup.add(exitItem);
//
//        // 3. Создание TrayIcon с прикрепленным PopupMenu
//        // ОС автоматически управляет его позиционированием при клике
//        trayIcon = new TrayIcon(image, "Desktop Control Telegram", popup);
//        trayIcon.setImageAutoSize(true);
//
//        // 4. Добавление в трей
//        tray.add(trayIcon);
//
//        // Дополнительный слушатель для двойного клика (например, для показа окна)
//        trayIcon.addActionListener(e -> System.out.println("Tray icon double-clicked (or single-clicked on some OS)"));
//
//        // ВАЖНО: Мы удалили слушатели MouseAdapter, mouseReleased и showSwingMenu,
//        // потому что AWT.PopupMenu обрабатывает их автоматически.
//    }
//
//    // --- Вспомогательные методы для слушателей ---
//
//    private ActionListener createRegenKeyListener() {
//        return e -> {
//            try {
//                String newKey = KeyManager.regenerateKey();
//                // AWT displayMessage работает независимо от Swing
//                trayIcon.displayMessage(
//                        "Desktop Control Telegram",
//                        "Новый ключ сгенерирован:\n" + newKey,
//                        TrayIcon.MessageType.INFO
//                );
//                System.out.println("Новый ключ: " + newKey);
//            } catch (Exception ex) {
//                trayIcon.displayMessage(
//                        "Error",
//                        "Не удалось перегенерировать ключ",
//                        TrayIcon.MessageType.ERROR
//                );
//            }
//        };
//    }
//
//    private ActionListener createExitListener(SystemTray tray) {
//        return e -> {
//            System.out.println("Shutting down...");
//            shutdown(tray);
//        };
//    }
//
//    // --- Методы завершения ---
//
//    /**
//     * Корректное завершение программы
//     */
//    private void shutdown(SystemTray tray) {
//        try {
//            if (client != null) {
//                client.close();
//            }
//            if (trayIcon != null) {
//                tray.remove(trayIcon);
//            }
//            // invokerFrame.dispose() больше не нужен
//        } catch (Exception ignored) {
//        }
//
//        wait.countDown(); // разблокирует start()
//        System.exit(0);
//    }
//
//    public void await() throws InterruptedException {
//        wait.await();
//    }
//}