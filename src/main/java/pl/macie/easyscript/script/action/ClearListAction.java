package pl.macie.easyscript.script.action;

import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.ScriptListUtil;
import pl.macie.easyscript.util.TextUtil;

public final class ClearListAction implements Action {
    private final String listName;
    private final SourceLocation source;

    public ClearListAction(String listName, SourceLocation source) {
        this.listName = listName;
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        ScriptListUtil.clear(context.getVariables(), TextUtil.applyPlaceholders(listName, context));
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }
}
