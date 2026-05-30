package pl.macie.easyscript.util;

import org.bukkit.ChatColor;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ColorUtil {
    private static final Pattern AMPERSAND_HEX = Pattern.compile("(?i)&?#([0-9a-f]{6})");
    private static final Pattern TAG_HEX = Pattern.compile("(?i)<#([0-9a-f]{6})>");

    private ColorUtil() {
    }

    public static String colorize(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        String withHex = replaceHexColors(input);
        return ChatColor.translateAlternateColorCodes('&', withHex);
    }

    private static String replaceHexColors(String input) {
        String text = replaceHexPattern(input, AMPERSAND_HEX);
        return replaceHexPattern(text, TAG_HEX);
    }

    private static String replaceHexPattern(String input, Pattern pattern) {
        Matcher matcher = pattern.matcher(input);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(buffer, Matcher.quoteReplacement(toSectionHex(matcher.group(1))));
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    private static String toSectionHex(String hex) {
        String normalized = hex.toLowerCase(Locale.ROOT);
        StringBuilder builder = new StringBuilder("§x");
        for (char character : normalized.toCharArray()) {
            builder.append('§').append(character);
        }
        return builder.toString();
    }
}
