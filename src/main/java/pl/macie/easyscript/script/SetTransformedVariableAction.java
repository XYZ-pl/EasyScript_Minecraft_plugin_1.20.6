package pl.macie.easyscript.script.action;

import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.TextUtil;

import java.util.Locale;

public final class SetTransformedVariableAction implements Action {
    private final String variableName;
    private final Mode mode;
    private final String input;
    private final String extraA;
    private final String extraB;
    private final SourceLocation source;

    public SetTransformedVariableAction(String variableName, Mode mode, String input, String extraA, String extraB, SourceLocation source) {
        this.variableName = variableName;
        this.mode = mode;
        this.input = input;
        this.extraA = extraA;
        this.extraB = extraB;
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        String value = TextUtil.applyPlaceholders(input, context);
        String result = switch (mode) {
            case LOWERCASE -> value.toLowerCase(Locale.ROOT);
            case UPPERCASE -> value.toUpperCase(Locale.ROOT);
            case TRIMMED -> value.trim();
            case ROUNDED -> rounded(value);
            case SUBSTRING -> substring(value, context);
            case REPLACE -> value.replace(TextUtil.applyPlaceholders(extraA, context), TextUtil.applyPlaceholders(extraB, context));
        };
        context.getVariables().set(TextUtil.applyPlaceholders(variableName, context), result);
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }

    private String rounded(String value) {
        try {
            return String.valueOf(Math.round(Double.parseDouble(value.replace(',', '.'))));
        } catch (NumberFormatException ignored) {
            return "0";
        }
    }

    private String substring(String value, ScriptContext context) {
        int from = parseIndex(TextUtil.applyPlaceholders(extraA, context), 1) - 1;
        int to = parseIndex(TextUtil.applyPlaceholders(extraB, context), value.length());
        int safeFrom = Math.max(0, Math.min(value.length(), from));
        int safeTo = Math.max(safeFrom, Math.min(value.length(), to));
        return value.substring(safeFrom, safeTo);
    }

    private int parseIndex(String value, int fallback) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    public enum Mode {
        LOWERCASE,
        UPPERCASE,
        TRIMMED,
        ROUNDED,
        SUBSTRING,
        REPLACE
    }
}
