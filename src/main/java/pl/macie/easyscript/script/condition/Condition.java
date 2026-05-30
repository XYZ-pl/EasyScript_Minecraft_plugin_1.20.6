package pl.macie.easyscript.script.condition;

import pl.macie.easyscript.script.model.ScriptContext;

public interface Condition {
    boolean test(ScriptContext context);

    default boolean canRunAsynchronously() {
        return false;
    }
}
