package pl.macie.easyscript.script.condition;

import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.TextUtil;

import java.util.Locale;

public final class VariableCondition implements Condition {
    private final Mode mode;
    private final String name;
    private final String expected;

    public VariableCondition(Mode mode, String name, String expected) {
        this.mode = mode;
        this.name = name;
        this.expected = expected;
    }

    @Override
    public boolean test(ScriptContext context) {
        String variableName = TextUtil.applyPlaceholders(name, context);
        String actual = context.getVariables().get(variableName).toLowerCase(Locale.ROOT);
        String preparedExpected = TextUtil.applyPlaceholders(expected, context).toLowerCase(Locale.ROOT);
        return switch (mode) {
            case CONTAINS -> actual.contains(preparedExpected);
            case EQUALS -> actual.equals(preparedExpected);
            case EXISTS -> !actual.isBlank();
        };
    }

    @Override
    public boolean canRunAsynchronously() {
        return true;
    }

    public enum Mode {
        CONTAINS,
        EQUALS,
        EXISTS
    }
}
