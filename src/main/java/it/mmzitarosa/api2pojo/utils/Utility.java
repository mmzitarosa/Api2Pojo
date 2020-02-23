package it.mmzitarosa.api2pojo.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utility {

    public static final String URL_REGEX = "^(?:(https?)\\:\\/\\/(?:([\\w\\-]+)\\.)+([\\w\\-]+))((?:\\/([\\w\\%\\-]+))*(?:(?:\\.[\\w\\%\\-]+)?\\?(?:\\&?[\\w\\%\\-]+\\=[\\w\\%\\-]+)+)?)?$";

    public static boolean isValidURL(String url) {
        Pattern pattern = Pattern.compile(URL_REGEX);
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            try {
                new URL(url);
            } catch (MalformedURLException e) {
                return false;
            }
            return true;
        }
        return false;
    }

    public static String capitalize(String string) {
        String s1 = string.substring(0, 1).toUpperCase();
        return s1 + string.substring(1); //Capitalize
    }

}
