package it.mmzitarosa.api2pojo.reflection;

import it.mmzitarosa.api2pojo.utils.Utility;
import org.jetbrains.annotations.Nullable;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReflectionUtility {

    private static final String URL_REGEX = "^(?:(https?)\\:\\/\\/(?:([\\w\\-]+)\\.)+([\\w\\-]+))((?:\\/([\\w\\%\\-]+))*(?:(?:\\.[\\w\\%\\-]+)?\\?(?:\\&?[\\w\\%\\-]+\\=[\\w\\%\\-]+)+)?)?$";

    private static final int PROTOCOL_INDEX = 1;
    private static final int DOMAIN_INDEX = 2;
    private static final int TLD_INDEX = 3;
    private static final int PATH_INDEX = 4;
    private static final int SERVLET_INDEX = 5;

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

    @Nullable
    public static String retrieveServletName(String url) {
        Matcher matcher = Pattern.compile(URL_REGEX).matcher(url);
        return matcher.find() ? matcher.group(SERVLET_INDEX) : null;
    }

    @Nullable
    public static String retrieveDomainName(String url) {
        Matcher matcher = Pattern.compile(URL_REGEX).matcher(url);
        return matcher.find() ? matcher.group(DOMAIN_INDEX) : null;
    }

    public static String retrievePackageName(String url) {
        Matcher matcher = Pattern.compile(URL_REGEX).matcher(url);
        if (matcher.find()) {
            String tdl = matcher.group(TLD_INDEX);
            String domain = matcher.group(DOMAIN_INDEX);
            String servlet = matcher.group(SERVLET_INDEX);
            return Utility.joinStrings('.', tdl, domain, "api2pojo", servlet);
        }
        return Utility.joinStrings('.', "it", "mmzitarosa", "api2pojo", "model");
    }

}
