package pl.macie.easyscript.script.condition;

import pl.macie.easyscript.script.model.ScriptContext;

public final class PlayerOpCondition implements Condition {
    @Override
    public boolean test(ScriptContext context) {
        return context.getPlayer() != null && context.getPlayer().isOp();
    }
}
