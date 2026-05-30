package pl.macie.easyscript.util;

import pl.macie.easyscript.script.variable.ScriptVariables;

import java.util.ArrayList;
import java.util.List;

public final class ScriptListUtil {
    private static final String PREFIX = "list:";
    private static final String SEPARATOR = "\u001F";

    private ScriptListUtil() {
    }

    public static List<String> values(ScriptVariables variables, String listName) {
        String raw = variables.get(key(listName));
        if (raw.isBlank()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(List.of(raw.split(SEPARATOR, -1)));
    }

    public static void add(ScriptVariables variables, String listName, String value) {
        List<String> values = values(variables, listName);
        values.add(value);
        save(variables, listName, values);
    }

    public static void remove(ScriptVariables variables, String listName, String value) {
        List<String> values = values(variables, listName);
        values.removeIf(entry -> entry.equalsIgnoreCase(value));
        save(variables, listName, values);
    }

    public static void clear(ScriptVariables variables, String listName) {
        variables.delete(key(listName));
    }

    public static boolean contains(ScriptVariables variables, String listName, String value) {
        for (String entry : values(variables, listName)) {
            if (entry.equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }

    public static int size(ScriptVariables variables, String listName) {
        return values(variables, listName).size();
    }

    public static String joined(ScriptVariables variables, String listName) {
        return String.join(", ", values(variables, listName));
    }

    private static void save(ScriptVariables variables, String listName, List<String> values) {
        if (values.isEmpty()) {
            clear(variables, listName);
        } else {
            variables.set(key(listName), String.join(SEPARATOR, values));
        }
    }

    private static String key(String listName) {
        return PREFIX + listName;
    }
}
