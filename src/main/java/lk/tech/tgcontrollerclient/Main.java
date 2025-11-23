package lk.tech.tgcontrollerclient;

import lk.tech.tgcontrollerclient.socket.ReactorWsClient;

public class Main {
    void main() throws Exception {
        ReactorWsClient client = new ReactorWsClient();
        client.connect("ws://localhost:8484/ws?key=CLIENT_001");
        Thread.currentThread().join();
    }
}