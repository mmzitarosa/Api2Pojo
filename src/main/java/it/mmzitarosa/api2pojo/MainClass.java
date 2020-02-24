package it.mmzitarosa.api2pojo;

import it.mmzitarosa.api2pojo.network.Network;
import it.mmzitarosa.api2pojo.network.NetworkCallback;
import it.mmzitarosa.api2pojo.reflection.Reflection;
import it.mmzitarosa.api2pojo.reflection.ReflectionException;
import it.mmzitarosa.api2pojo.reflection.ReflectionUtility;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;

public class MainClass {

    private static final String TITLE = "Api2Pojo";

    public static File destination = null;
    public static String apiToken = null;
    private static Network network;
    private static String targetName;

    public static void main(String[] args) {
        boolean repeat = true;
        String url = "";
        while (repeat) {
            url = readUserInput("URL: ");
            if (ReflectionUtility.isValidURL(url)) {
                repeat = false;
            } else {
                showOutputMessage("URL not valid.", JOptionPane.ERROR_MESSAGE);
            }
        }

        network = new Network();

        repeat = true;
        while (repeat) {
            destination = readUserChoice("Destination Project: ");
            try {
                ReflectionUtility.verifyProjectFolder(destination);
                repeat = false;
            } catch (ReflectionException e) {
                showOutputMessage(e.getMessage(), JOptionPane.ERROR_MESSAGE);
            }
        }

        network.setXAuthToken(readUserInput("Auth Token: "));

        targetName = ReflectionUtility.retrieveServletName(url);

        repeat = true;
        do {

            try {
                ReflectionUtility.verifyClassName(targetName);
                repeat = false;
            } catch (ReflectionException e) {
                showOutputMessage(e.getMessage(), JOptionPane.ERROR_MESSAGE);
                targetName = readUserInput("Main class name: ", targetName);
            }
        } while (repeat);

        network.doGet(url, new NetworkCallback() {
            @Override
            public void onSuccess(String responseString, String url) {
                try {
                    Reflection reflection = new Reflection(url, responseString);
                    if (destination != null) {
                        reflection.setProject(destination);
                    }
                    reflection.generateClass(targetName);
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
    }

    private static String readUserInput(String message, String defaultValue) {
        JFrame frame = new JFrame(TITLE);
        String input;
        if (defaultValue != null) {
            input = JOptionPane.showInputDialog(frame, message, defaultValue);
        } else {
            input = JOptionPane.showInputDialog(frame, message, defaultValue);
        }
        if (input == null)
            System.exit(0);
        return input;
    }

    private static void showOutputMessage(String message, int option) {
        JFrame frame = new JFrame(TITLE);
        JOptionPane.showMessageDialog(frame, message, TITLE, option);

    }

    private static String readUserInput(String message) {
        return readUserInput(message, null);
    }

    private static File readUserChoice(String message) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        int option = fileChooser.showOpenDialog(null);
        File f = null;
        if (option == JFileChooser.APPROVE_OPTION) {
            f = fileChooser.getSelectedFile();
            if (!f.isDirectory()) {
                f = f.getParentFile();
            }
        } else {
            System.exit(0);
        }
        return f;
    }

}
