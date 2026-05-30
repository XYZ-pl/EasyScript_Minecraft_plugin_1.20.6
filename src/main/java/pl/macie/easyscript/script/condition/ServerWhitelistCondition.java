package pl.macie.easyscript.script.condition;

import pl.macie.easyscript.script.model.ScriptContext;

public final class ServerWhitelistCondition implements Condition {
    @Override
    public boolean test(ScriptContext context) {
        return context.getPlugin().getServer().hasWhitelist();
    }

    @Override
    public boolean canRunAsynchronously() {
        return true;
    }
}
