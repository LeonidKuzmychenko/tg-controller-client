//package lk.tech.tgcontrollerclient.old;
//
//import java.awt.*;
//
//public class DesktopApp {
//
//    private DesktopClient client;
//    private volatile boolean running = true;
//
//    public void start() throws Exception {
//
//        client = new DesktopClient();
//        client.connect("ws://localhost:8484/ws", "CLIENT_001");
//
//        setupTrayIcon();
//
//        // Запускаем фоновый поток, чтобы JVM жила всегда
//        startKeepAliveThread();
//    }
//
//    private void startKeepAliveThread() {
//        Thread keepAlive = new Thread(() -> {
//            while (running) {
//                try {
//                    Thread.sleep(1000); // 1 секунда
//                } catch (InterruptedException ignored) {}
//            }
//        });
//
//        keepAlive.setDaemon(false); // JVM НЕ завершится
//        keepAlive.start();
//    }
//
//    private void setupTrayIcon() throws AWTException {
//        if (!SystemTray.isSupported()) {
//            throw new RuntimeException("System tray not supported!");
//        }
//
//        SystemTray tray = SystemTray.getSystemTray();
//        Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
//
//        PopupMenu menu = new PopupMenu();
//
//        MenuItem exitItem = new MenuItem("Exit");
//        exitItem.addActionListener(e -> {
//            shutdown();
//        });
//
//        menu.add(exitItem);
//
//        TrayIcon trayIcon = new TrayIcon(image, "Desktop Control Telegram", menu);
//        trayIcon.setImageAutoSize(true);
//        tray.add(trayIcon);
//    }
//
//    private void shutdown() {
//        try {
//            running = false;
//            if (client != null) {
//                client.close();
//            }
//        } catch (Exception ignored) {
//        }
//
//        System.exit(0);
//    }
//}
