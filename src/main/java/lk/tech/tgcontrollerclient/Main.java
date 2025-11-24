package lk.tech.tgcontrollerclient;


import lk.tech.tgcontrollerclient.socket.ReactorWsClient;
import lk.tech.tgcontrollerclient.ui.MyUI;

public class Main {

    void main() throws Exception {
        ReactorWsClient.INSTANCE.init();
        ReactorWsClient.INSTANCE.safeConnect();

        MyUI.INSTANCE.start();
        MyUI.INSTANCE.await();
    }
}
