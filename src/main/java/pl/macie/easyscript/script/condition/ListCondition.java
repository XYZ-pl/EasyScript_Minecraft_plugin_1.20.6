package pl.macie.easyscript.script.condition;

import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.ScriptListUtil;
import pl.macie.easyscript.util.TextUtil;

public final class ListCondition implements Condition {
    private final String listName;
    private final String value;
    private final Mode mode;

    public ListCondition(String listName, String value, Mode mode) {
        this.listName = listName;
        this.value = value;
        this.mode = mode;
    }

    @Override
    public boolean test(ScriptContext context) {
        String preparedList = TextUtil.applyPlaceholders(listName, context);
        return switch (mode) {
            case CONTAINS -> ScriptListUtil.contains(context.getVariables(), preparedList, TextUtil.applyPlaceholders(value, context));
            case EXISTS -> ScriptListUtil.size(context.getVariables(), preparedList) > 0;
            case EMPTY -> ScriptListUtil.size(context.getVariables(), preparedList) == 0;
        };
    }

    public enum Mode {
        CONTAINS,
        EXISTS,
        EMPTY
    }
}
