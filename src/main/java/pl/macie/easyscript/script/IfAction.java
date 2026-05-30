package pl.macie.easyscript.script.action;

import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.condition.Condition;
import pl.macie.easyscript.script.model.ScriptContext;

public final class IfAction implements Action {
    private final Condition condition;
    private final Action nestedAction;
    private final SourceLocation source;

    public IfAction(Condition condition, Action nestedAction, SourceLocation source) {
        this.condition = condition;
        this.nestedAction = nestedAction;
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        if (condition.test(context)) {
            nestedAction.execute(context);
        }
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }

    @Override
    public boolean canRunAsynchronously() {
        return condition.canRunAsynchronously() && nestedAction.canRunAsynchronously();
    }
}
