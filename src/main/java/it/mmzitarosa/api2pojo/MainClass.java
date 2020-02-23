package it.mmzitarosa.api2pojo;

import it.mmzitarosa.api2pojo.network.Network;
import it.mmzitarosa.api2pojo.network.NetworkCallback;
import it.mmzitarosa.api2pojo.reflection.Reflection;
import it.mmzitarosa.api2pojo.reflection.ReflectionException;
import it.mmzitarosa.api2pojo.reflection.ReflectionUtility;
import org.jetbrains.annotations.Nullable;

public class MainClass {

    public static String destProject = null;
    private static Network network;

    public static void main(String[] args) {
        String url = "";
        if (args.length == 0 || args[0].equalsIgnoreCase("-h")) {
            printHelper();
            System.exit(0);
        }

        if (args.length > 2) {
            System.err.println("Incompatible number of fields.");
            System.exit(-1);
        }

        network = new Network();
        url = args[0];

        if (args.length == 2) {
            destProject = args[1];
        }

        if (ReflectionUtility.isValidURL(url)) {
            network.doGet(url, new NetworkCallback() {
                @Override
                public void onSuccess(String responseString, String url) {
                    try {
                        Reflection reflection = new Reflection(url, responseString);
                        if (destProject != null) {
                            reflection.setProjectPath(destProject);
                        }
                        reflection.generateClass();
                        System.exit(0);
                    } catch (ReflectionException e) {
                        e.printStackTrace();
                        System.exit(-1);
                    }
                }

                @Override
                public void onError(String s, @Nullable Exception e) {
                    System.err.println(s);
                    if (e != null) e.printStackTrace();
                    System.exit(-1);
                }
            });
        } else {
            System.err.println("URL not valid.");
            System.exit(-1);
        }
    }

    private static void printHelper() {
        System.out.println("API_URL [PROJECT_PATH]");
    }

}
