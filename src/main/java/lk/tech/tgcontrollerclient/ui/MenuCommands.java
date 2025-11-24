package lk.tech.tgcontrollerclient.ui;

import lk.tech.tgcontrollerclient.socket.ReactorWsClient;
import lk.tech.tgcontrollerclient.socket.ReconnectManager;
import lk.tech.tgcontrollerclient.utils.BaseProvider;
import lk.tech.tgcontrollerclient.utils.KeyManager;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.datatransfer.StringSelection;

@Slf4j
public enum MenuCommands {

    INSTANCE;

    public void shutdown() {
        log.info("Shutdown");
        ReconnectManager.INSTANCE.close();
        ReactorWsClient.INSTANCE.close();
        TrayService.INSTANCE.close();
        MenuService.INSTANCE.close();
        MyUI.INSTANCE.close();
    }

    public void regenerateKey() {
        log.info("Regenerate key");
        KeyManager.INSTANCE.regenerateKey();
        ReactorWsClient.INSTANCE.reloadKeyAndReconnect();
    }

    public void copyKey() {
        log.info("Copy key");
        String key = BaseProvider.key();
        StringSelection selection = new StringSelection(key);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
    }
}
