package lk.tech.tgcontrollerclient;


import lk.tech.tgcontrollerclient.socket.ReactorWsClient;
import lk.tech.tgcontrollerclient.ui.MyUI;
import lk.tech.tgcontrollerclient.utils.KeyManager;

public class Main {

    void main() throws Exception {
        KeyManager.INSTANCE.init();

        ReactorWsClient.INSTANCE.init();
        ReactorWsClient.INSTANCE.safeConnect();

        MyUI.INSTANCE.start();
        MyUI.INSTANCE.await();
    }
}
