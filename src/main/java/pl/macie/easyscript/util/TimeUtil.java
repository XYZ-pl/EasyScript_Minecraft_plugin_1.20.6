package pl.macie.easyscript.util;

import java.util.Locale;

public final class TimeUtil {
    private TimeUtil() {
    }

    public static long parseTicks(String input) {
        if (input == null || input.isBlank()) {
            return -1L;
        }

        String[] parts = input.trim().toLowerCase(Locale.ROOT).split("\\s+");
        if (parts.length == 0) {
            return -1L;
        }

        double amount;
        try {
            amount = Double.parseDouble(parts[0].replace(',', '.'));
        } catch (NumberFormatException exception) {
            return -1L;
        }

        String unit = parts.length == 1 ? "ticks" : parts[1];
        if (unit.startsWith("tick")) {
            return Math.max(0L, Math.round(amount));
        }
        if (unit.startsWith("second") || unit.equals("s") || unit.startsWith("sec")) {
            return Math.max(0L, Math.round(amount * 20.0));
        }
        if (unit.startsWith("minute") || unit.equals("m") || unit.startsWith("min")) {
            return Math.max(0L, Math.round(amount * 20.0 * 60.0));
        }
        if (unit.startsWith("hour") || unit.equals("h")) {
            return Math.max(0L, Math.round(amount * 20.0 * 60.0 * 60.0));
        }
        return -1L;
    }

    public static String formatShort(long ticks) {
        long seconds = Math.max(0L, Math.round(ticks / 20.0));
        if (seconds < 60) {
            return seconds + "s";
        }
        long minutes = seconds / 60;
        long restSeconds = seconds % 60;
        if (minutes < 60) {
            return restSeconds == 0 ? minutes + "m" : minutes + "m " + restSeconds + "s";
        }
        long hours = minutes / 60;
        long restMinutes = minutes % 60;
        return restMinutes == 0 ? hours + "h" : hours + "h " + restMinutes + "m";
    }
}
