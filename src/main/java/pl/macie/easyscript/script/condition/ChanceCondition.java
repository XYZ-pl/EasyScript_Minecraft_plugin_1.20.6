package pl.macie.easyscript.script.condition;

import pl.macie.easyscript.script.model.ScriptContext;

import java.util.concurrent.ThreadLocalRandom;

public final class ChanceCondition implements Condition {
    private final double percent;

    public ChanceCondition(double percent) {
        this.percent = Math.max(0.0, Math.min(100.0, percent));
    }

    @Override
    public boolean test(ScriptContext context) {
        return ThreadLocalRandom.current().nextDouble(100.0) < percent;
    }

    @Override
    public boolean canRunAsynchronously() {
        return true;
    }
}
