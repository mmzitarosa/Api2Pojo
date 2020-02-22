package it.mmzitarosa.api2pojo;

import it.mmzitarosa.api2pojo.utils.Utility;
import org.json.JSONArray;
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

    public static String newGenerateClass(Object object, String fileName) throws IOException {
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

    private static File createClass(String name) throws IOException {
        name = Utility.capitalize(name);
        File classFile = new File("C:\\Users\\matte\\IdeaProjects\\Api2Pojo\\src\\main\\java\\it\\mmzitarosa\\api2pojo\\model", name + ".java");

        // generate the source code, using the source filename as the class name
        String sourceCode = "package it.mmzitarosa.api2pojo.model;\n\n" +
                "public class " + name + " {\n\n" +
                "}";

        FileWriter writer = new FileWriter(classFile);
        writer.write(sourceCode);
        writer.close();

        return classFile;
    }

    private static void addField(String type, String name, File classFile) {
        addField(type, name, null, classFile);
    }

    private static void addField(String type, String name, String value, File classFile) {
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
            Files.write(Paths.get(classFile.getAbsolutePath()), join(lines).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void addConstructors(File classFile) {
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

            Files.write(Paths.get(classFile.getAbsolutePath()), join(lines).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Map<String, String> containsList(Map<String, String> map) {
        Map<String, String> result = new HashMap<>();
        for (String key : map.keySet()) {
            if (map.get(key).startsWith("List")) {
                result.put(key, map.get(key));
            }
        }
        return (result.size() != 0) ? result : null;
    }

    private static void addGetterSetter(File classFile) {
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

            Files.write(Paths.get(classFile.getAbsolutePath()), join(lines).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String join(String[] lines) {
        StringBuilder text = new StringBuilder();
        for (Object line : lines) {
            text.append((String) line);
            text.append("\n");
        }
        return text.toString();
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