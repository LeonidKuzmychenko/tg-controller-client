package lk.tech.tgcontrollerclient.utils;

public class BaseProvider {

    public static String key(){
        return "CLIENT_001";
    }

    public static String socketUrl(){
        return "ws://localhost:8484/ws";
    }

    public static String httpUrl(){
        return "http://localhost:8282";
    }
}
