package lk.tech.tgcontrollerclient;

import lk.tech.tgcontrollerclient.socket.ReactorWsClient;
import lk.tech.tgcontrollerclient.ui.UiNew;

public class Main {

    void main() throws Exception {
        ReactorWsClient client = new ReactorWsClient();
        client.safeConnect();

        UiNew uiNew = new UiNew(client);
        uiNew.setupTrayIcon();
        uiNew.await();
    }
}
