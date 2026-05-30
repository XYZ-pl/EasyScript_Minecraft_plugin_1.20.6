package pl.macie.easyscript.script.action;

import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.ScriptListUtil;
import pl.macie.easyscript.util.TextUtil;

public final class AddToListAction implements Action {
    private final String value;
    private final String listName;
    private final SourceLocation source;

    public AddToListAction(String value, String listName, SourceLocation source) {
        this.value = value;
        this.listName = listName;
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        ScriptListUtil.add(context.getVariables(), TextUtil.applyPlaceholders(listName, context), TextUtil.applyPlaceholders(value, context));
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }
}
