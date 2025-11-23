package lk.tech.tgcontrollerclient;

import lk.tech.tgcontrollerclient.socket.ReactorWsClient;
import lk.tech.tgcontrollerclient.ui.UI;

public class Main {

    void main() throws Exception {
        ReactorWsClient client = new ReactorWsClient();
        client.safeConnect();

        UI ui = new UI(client);
        ui.setupTrayIcon();
        ui.await();
    }
}
