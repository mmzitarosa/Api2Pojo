package it.mmzitarosa.api2pojo.reflection;

public class ReflectionException extends Exception {

    public ReflectionException(Exception e) {
        super(e);
    }

    public ReflectionException(String s) {
        super(s);
    }
}
