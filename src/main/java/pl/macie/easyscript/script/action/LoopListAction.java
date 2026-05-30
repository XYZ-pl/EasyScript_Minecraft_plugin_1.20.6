package pl.macie.easyscript.script.action;

import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.ScriptListUtil;
import pl.macie.easyscript.util.TextUtil;

import java.util.List;
import java.util.Map;

public final class LoopListAction implements Action {
    private final String listName;
    private final List<Action> actions;
    private final SourceLocation source;

    public LoopListAction(String listName, List<Action> actions, SourceLocation source) {
        this.listName = listName;
        this.actions = List.copyOf(actions);
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        String preparedList = TextUtil.applyPlaceholders(listName, context);
        List<String> values = ScriptListUtil.values(context.getVariables(), preparedList);
        for (int index = 0; index < values.size(); index++) {
            ScriptContext loopContext = context.withVariables(Map.of(
                    "loop-value", values.get(index),
                    "loop-number", String.valueOf(index + 1),
                    "loop-index", String.valueOf(index + 1)
            ));
            for (Action action : actions) {
                action.execute(loopContext);
            }
        }
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }
}
