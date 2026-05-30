package pl.macie.easyscript.script.condition;

import pl.macie.easyscript.script.model.ScriptContext;

public final class NotCondition implements Condition {
    private final Condition nested;

    public NotCondition(Condition nested) {
        this.nested = nested;
    }

    @Override
    public boolean test(ScriptContext context) {
        return !nested.test(context);
    }

    @Override
    public boolean canRunAsynchronously() {
        return nested.canRunAsynchronously();
    }
}
