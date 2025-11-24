package lk.tech.tgcontrollerclient.ui;

import lk.tech.tgcontrollerclient.socket.ReactorWsClient;
import lk.tech.tgcontrollerclient.socket.ReconnectManager;
import lk.tech.tgcontrollerclient.utils.BaseProvider;
import lk.tech.tgcontrollerclient.utils.KeyManager;

import java.awt.*;
import java.awt.datatransfer.StringSelection;

public enum MenuCommands {

    INSTANCE;

    public void shutdown() {
        System.out.println("Shutdown");
        ReconnectManager.INSTANCE.close();
        ReactorWsClient.INSTANCE.close();
        TrayService.INSTANCE.close();
        MenuService.INSTANCE.close();
        MyUI.INSTANCE.close();
    }

    public void regenerateKey() {
        System.out.println("Regenerate key");
        KeyManager.INSTANCE.regenerateKey();
        ReactorWsClient.INSTANCE.reloadKeyAndReconnect();
    }

    public void copyKey() {
        System.out.println("Copy key");
        String key = BaseProvider.key();
        StringSelection selection = new StringSelection(key);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
    }
}
