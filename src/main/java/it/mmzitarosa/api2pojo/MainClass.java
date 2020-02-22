package it.mmzitarosa.api2pojo;

import it.mmzitarosa.api2pojo.utils.Utility;
import org.json.JSONArray;

import java.io.IOException;

public class MainClass {

    public static void main(String[] args) {
        String url = "";
        if (args.length == 0 || args[0].equalsIgnoreCase("-h")) {
            printHelper();
            System.exit(0);
        }
        url = args[0];
        if (Utility.isValidURL(url)) {
            try {
                String content = "";
                //TODO network call to url
                Reflection.newGenerateClass(new JSONArray(content), "beers");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("false");
        }
    }

    private static void printHelper() {
        System.out.println("API_URL [PROJECT_PATH PACKAGE_PATH]");
    }

}
