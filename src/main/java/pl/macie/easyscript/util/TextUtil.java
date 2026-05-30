package pl.macie.easyscript.util;

import pl.macie.easyscript.script.model.ScriptContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TextUtil {
    private static final Pattern PLACEHOLDER = Pattern.compile("\\{([a-zA-Z0-9_:\\-.]+)}");

    private TextUtil() {
    }

    public static String applyPlaceholders(String input, ScriptContext context) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        Matcher matcher = PLACEHOLDER.matcher(input);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(buffer, Matcher.quoteReplacement(context.placeholder(matcher.group(1))));
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    public static String escapePercent(String input) {
        return input == null ? "" : input.replace("%", "%%");
    }
}
