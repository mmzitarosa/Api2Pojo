package it.mmzitarosa.api2pojo.network;

public class NetworkException extends Exception {

    public NetworkException(Exception e) {
        super(e);
    }

    public NetworkException(String s) {
        super(s);
    }
}
