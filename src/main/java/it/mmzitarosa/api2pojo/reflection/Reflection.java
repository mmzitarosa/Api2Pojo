package it.mmzitarosa.api2pojo.reflection;

import it.mmzitarosa.api2pojo.utils.Utility;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Reflection {

    private String url;
//    private String servlet;
    private String content;
    private File destinationFolder;
    private String innerPath;

    public Reflection(String url, String content) {
        this.url = url;
        this.content = content;
        innerPath = ReflectionUtility.retrievePackageName(url).replace(".", "/");

        destinationFolder = new File(System.getProperty("user.dir"), "out/" + innerPath);
        if (!destinationFolder.exists() || !destinationFolder.isDirectory()) {
            if (!destinationFolder.mkdirs()) {
                destinationFolder = new File(System.getProperty("user.dir"));
            }
        }
    }

    public void setProject(File file) throws ReflectionException {
        File src = ReflectionUtility.verifyProjectFolder(file);
        this.destinationFolder = new File(src, innerPath);
        if (!destinationFolder.exists() || !destinationFolder.isDirectory()) {
            if (!destinationFolder.mkdirs()) {
                destinationFolder = src;
            }
        }
    }

    public void generateClass(String className) throws ReflectionException {
        try {
            try {
                newGenerateClass(new JSONObject(content), className);
            } catch (JSONException e) {
                newGenerateClass(new JSONArray(content), className);
            }
        } catch (Exception e) {
            throw new ReflectionException(e);
        }
    }

    private String newGenerateClass(Object object, String fileName) throws IOException {
        File file;
        if (object == null) {
            return Object.class.getSimpleName();
        }
        Map<String, Object> jsonMap;
        switch (object.getClass().getSimpleName()) {
            case "JSONObject":
                file = createClass(fileName);
                jsonMap = ((JSONObject) object).toMap();
                for (String key : jsonMap.keySet()) {
                    addField(newGenerateClass(jsonMap.get(key), key), key, file);
                }
                break;
            case "HashMap":
                file = createClass(fileName);
                jsonMap = (HashMap<String, Object>) object;
                for (String key : jsonMap.keySet()) {
                    addField(newGenerateClass(jsonMap.get(key), key), key, file);
                }
                break;
            case "JSONArray":
                file = createClass(fileName);
                if (!((JSONArray) object).isEmpty())
                    addField("List<" + newGenerateClass(((JSONArray) object).get(0), fileName + Utility.capitalize("item")) + ">", fileName + Utility.capitalize("item"), file);
                break;
            case "ArrayList":
            case "List":
                if (!((List<?>) object).isEmpty())
                    return "List<" + newGenerateClass(((List<?>) object).get(0), fileName) + ">";
                else
                    return "List<Object>";
            default:
                if (object instanceof Number) {
                    return "Number";
                }
                return object.getClass().getSimpleName();
        }

        addConstructors(file);
        addGetterSetter(file);

        return nameFromClassFile(file);
    }

    private File createClass(String name) throws IOException {
        name = Utility.capitalize(name);
        File classFile = new File(destinationFolder, name + ".java");

        // generate the source code, using the source filename as the class name
        String sourceCode = Utility.generateHeaderComment() + "\n\n" + "package " + ReflectionUtility.retrievePackageName(url) + ";\n\n" +
                "public class " + name + " {\n\n" +
                "}";

        FileWriter writer = new FileWriter(classFile);
        writer.write(sourceCode);
        writer.close();

        return classFile;
    }

    private void addField(String type, String name, File classFile) {
        addField(type, name, null, classFile);
    }

    private void addField(String type, String name, String value, File classFile) {
        try {
            String files = new String(Files.readAllBytes(Paths.get(classFile.getAbsolutePath())));
            String[] lines = files.split("\n");

            if (type.contains("List<")) {
                if (!files.contains("import java.util.List;")) {
                    lines[2] = "import java.util.ArrayList;\nimport java.util.List;\n\n" + lines[2];
                }
            }

            String s1 = name.substring(0, 1).toLowerCase();
            name = s1 + name.substring(1); //CamelCase
            if (value != null) {
                lines[lines.length - 1] = "    private " + type + " " + name + " = " + value + ";\n" + lines[lines.length - 1];
            } else {
                lines[lines.length - 1] = "    private " + type + " " + name + ";\n" + lines[lines.length - 1];
            }
            Files.write(Paths.get(classFile.getAbsolutePath()), Utility.joinStrings('\n', lines).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addConstructors(File classFile) {
        try {
            String files = new String(Files.readAllBytes(Paths.get(classFile.getAbsolutePath())));
            String[] lines = files.split("\n");
            Map<String, String> classAttributes = getClassAttributes(classFile);
            Map<String, String> listItems;

            String constructor = "\n" + "    public " + nameFromClassFile(classFile) + "() {\n";
            if ((listItems = containsList(classAttributes)) != null) {
                for (String key : listItems.keySet()) {
                    constructor += "        this." + key + " = new Array" + listItems.get(key) + "();\n";
                }
            }
            constructor += "    }\n\n";

            constructor += "    public " + nameFromClassFile(classFile) + "(";

            for (String key : classAttributes.keySet()) {
                constructor += classAttributes.get(key) + " " + key + ", ";
            }
            constructor = constructor.substring(0, constructor.length() - 2) + ") {\n";
            for (String key : classAttributes.keySet()) {
                constructor += "        this." + key + " = " + key + ";\n";
            }
            constructor += "    }";

            lines[lines.length - 1] = constructor + "\n" + lines[lines.length - 1];

            Files.write(Paths.get(classFile.getAbsolutePath()), Utility.joinStrings('\n', lines).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Map<String, String> containsList(Map<String, String> map) {
        Map<String, String> result = new HashMap<>();
        for (String key : map.keySet()) {
            if (map.get(key).startsWith("List")) {
                result.put(key, map.get(key));
            }
        }
        return (result.size() != 0) ? result : null;
    }

    private void addGetterSetter(File classFile) {
        try {
            String files = new String(Files.readAllBytes(Paths.get(classFile.getAbsolutePath())));
            String[] lines = files.split("\n");

            String getterSetter = "\n";

            Map<String, String> classAttributes = getClassAttributes(classFile);
            for (String key : classAttributes.keySet()) {
                getterSetter += "    public " + classAttributes.get(key) + " get" + Utility.capitalize(key) + "() {\n" +
                        "        return this." + key + ";\n" +
                        "    }\n\n";
                getterSetter += "    public void set" + Utility.capitalize(key) + "(" + classAttributes.get(key) + " " + key + ") {\n" +
                        "        this." + key + " = " + key + ";\n" +
                        "    }\n\n";
            }

            lines[lines.length - 1] = getterSetter + lines[lines.length - 1];

            Files.write(Paths.get(classFile.getAbsolutePath()), Utility.joinStrings('\n', lines).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String nameFromClassFile(File classFile) {
        return classFile.getName().split("\\.")[0];
    }

    private static Map<String, String> getClassAttributes(File classFile) throws IOException {
        String files = new String(Files.readAllBytes(Paths.get(classFile.getAbsolutePath())));
        Map<String, String> map = new HashMap<>();
        Pattern pattern = Pattern.compile("(?<=private )\\S+\\s\\w+(?= ?[\\;\\=])");
        Matcher matcher = pattern.matcher(files);
        while (matcher.find()) {
            String found = matcher.group();
            String[] attribute = found.split(" ");
            map.put(attribute[1], attribute[0]);
        }
        return map;
    }

}