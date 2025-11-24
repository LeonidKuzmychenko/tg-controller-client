package lk.tech.tgcontrollerclient.ui;

import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.io.Closeable;

@Slf4j
public enum MenuService implements Closeable {

    INSTANCE;  // ← singleton

    private final JFrame invokerFrame;

    MenuService() {
        invokerFrame = new JFrame();
        invokerFrame.setUndecorated(true);
        invokerFrame.setOpacity(0f);
        invokerFrame.setType(Window.Type.UTILITY);
        invokerFrame.setAlwaysOnTop(true);
        invokerFrame.setSize(1, 1);
    }

    public void showPopupMenu() {
        // Закрытие старых меню
        MenuSelectionManager.defaultManager().clearSelectedPath();

        JPopupMenu menu = new JPopupMenu();

        JMenuItem copy = new JMenuItem("Copy key");
        copy.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        copy.addActionListener(a -> MenuCommands.INSTANCE.copyKey());
        menu.add(copy);

        menu.addSeparator();

        JMenuItem regen = new JMenuItem("Regenerate key");
        regen.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        regen.addActionListener(a -> MenuCommands.INSTANCE.regenerateKey());
        menu.add(regen);

        menu.addSeparator();

        JMenuItem exit = new JMenuItem("Exit");
        exit.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        exit.addActionListener(a -> MenuCommands.INSTANCE.shutdown());
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
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                //no realize
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
                invokerFrame.setVisible(false);
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                invokerFrame.setVisible(false);
            }
        });
    }

    @Override
    public void close() {
        try {
            log.info("[Shutdown] Hiding invokerFrame...");
            if (invokerFrame != null) {
                invokerFrame.setVisible(false);
                invokerFrame.dispose();
            }
        } catch (Exception e) {
            log.info("[Shutdown] Failed hiding invokerFrame: " + e);
        }
    }
}
