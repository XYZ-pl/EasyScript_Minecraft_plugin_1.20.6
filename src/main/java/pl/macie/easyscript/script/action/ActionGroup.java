package pl.macie.easyscript.script.action;

import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;

import java.util.List;

public final class ActionGroup implements Action {
    private final List<Action> actions;
    private final SourceLocation source;

    public ActionGroup(List<Action> actions, SourceLocation source) {
        this.actions = List.copyOf(actions);
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        for (Action action : actions) {
            action.execute(context);
        }
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }

    @Override
    public boolean canRunAsynchronously() {
        return actions.stream().allMatch(Action::canRunAsynchronously);
    }
}
