package it.mmzitarosa.api2pojo.utils;

public class Utility {

    public static String capitalize(String string) {
        String s1 = string.substring(0, 1).toUpperCase();
        return s1 + string.substring(1); //Capitalize
    }

    public static String joinStrings(char joinChar, String... strings) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < strings.length - 1; i++) {
            stringBuilder.append(strings[i]).append(joinChar);
        }
        stringBuilder.append(strings[strings.length - 1]);
        return stringBuilder.toString();
    }

}
