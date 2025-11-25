package lk.tech.tgcontrollerclient;


import lk.tech.tgcontrollerclient.socket.ReactorWsClient;
import lk.tech.tgcontrollerclient.ui.MyUI;
import lk.tech.tgcontrollerclient.utils.KeyManager;

public class Main {

    public static void main(String[] args) throws Exception {
        KeyManager.INSTANCE.init();
        ReactorWsClient.INSTANCE.init();
        ReactorWsClient.INSTANCE.safeConnect();

        MyUI.INSTANCE.await();
    }
}
