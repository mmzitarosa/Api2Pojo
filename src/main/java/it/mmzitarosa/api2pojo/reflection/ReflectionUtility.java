package it.mmzitarosa.api2pojo.reflection;

import it.mmzitarosa.api2pojo.utils.Utility;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
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

    public static File verifyProjectFolder(File file) throws ReflectionException {
        if (!file.isDirectory()) {
            throw new ReflectionException("Project file (\"" + file.getAbsolutePath() + "\") must be a directory. ");
        }
        if (file.listFiles() != null && Objects.requireNonNull(file.listFiles()).length == 0) {
            throw new ReflectionException("Project directory (\"" + file.getAbsolutePath() + "\") is empty.");
        }
        File src = new File(file, "src/main/java");
        if (!src.exists()) {
            src = new File(file, "app/src/main/java/");
        }
        if (!src.exists()) {
            throw new ReflectionException("Cannot find \"src/main/java\" directory.");
        }
        return src;
    }

    public static void verifyClassName(String className) throws ReflectionException {
        if (className == null || className.isEmpty()) {
            throw new ReflectionException("Class name (\"" + className + "\") is null or empty.");
        }
        Pattern pattern = Pattern.compile("^[a-zA-Z][\\w]*$");
        Matcher matcher = pattern.matcher(className);
        if (!matcher.find()) {
            throw new ReflectionException("Class name not valid, must start with a letter [A-Za-z].");
        }
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
            return Utility.joinStrings('.', tdl, domain, "api2pojo").replace("-", "_");
        }
        return Utility.joinStrings('.', "it", "mmzitarosa", "api2pojo").replace("-", "_");
    }

}
