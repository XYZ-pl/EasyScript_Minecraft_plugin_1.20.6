package pl.macie.easyscript.script.action;

import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.condition.Condition;
import pl.macie.easyscript.script.model.ScriptContext;

import java.util.List;

public final class BlockIfAction implements Action {
    private final Condition condition;
    private final List<Action> thenActions;
    private final List<Action> elseActions;
    private final SourceLocation source;

    public BlockIfAction(Condition condition, List<Action> thenActions, List<Action> elseActions, SourceLocation source) {
        this.condition = condition;
        this.thenActions = List.copyOf(thenActions);
        this.elseActions = List.copyOf(elseActions);
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        List<Action> actions = condition.test(context) ? thenActions : elseActions;
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
        return condition.canRunAsynchronously()
                && thenActions.stream().allMatch(Action::canRunAsynchronously)
                && elseActions.stream().allMatch(Action::canRunAsynchronously);
    }
}
