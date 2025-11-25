package lk.tech.tgcontrollerclient;


import lk.tech.tgcontrollerclient.socket.OkHttpWsClient;
import lk.tech.tgcontrollerclient.ui.MyUI;
import lk.tech.tgcontrollerclient.utils.KeyManager;

public class Main {

    public static void main(String[] args) throws Exception {
        KeyManager.INSTANCE.init();
        OkHttpWsClient.INSTANCE.init();
        OkHttpWsClient.INSTANCE.safeConnect();
        MyUI.INSTANCE.await();
    }
}
