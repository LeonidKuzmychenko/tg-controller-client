package lk.tech.tgcontrollerclient;

import lk.tech.tgcontrollerclient.socket.ReactorWsClient;
import lk.tech.tgcontrollerclient.ui.MyUI;

public class Main {

    void main() throws Exception {
//        ReactorWsClient client = new ReactorWsClient();
//        client.safeConnect();

        MyUI ui = new MyUI(null);
        ui.setupTrayIcon();
        ui.await();
    }
}
