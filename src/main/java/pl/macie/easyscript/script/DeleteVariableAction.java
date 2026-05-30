package pl.macie.easyscript.script.action;

import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.TextUtil;

public final class DeleteVariableAction implements Action {
    private final String name;
    private final SourceLocation source;

    public DeleteVariableAction(String name, SourceLocation source) {
        this.name = name;
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        context.getVariables().delete(TextUtil.applyPlaceholders(name, context));
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }
}
