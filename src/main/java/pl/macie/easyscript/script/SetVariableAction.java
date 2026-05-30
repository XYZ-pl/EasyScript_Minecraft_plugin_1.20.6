package pl.macie.easyscript.script.action;

import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.TextUtil;

public final class SetVariableAction implements Action {
    private final String name;
    private final String value;
    private final SourceLocation source;

    public SetVariableAction(String name, String value, SourceLocation source) {
        this.name = name;
        this.value = value;
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        context.getVariables().set(
                TextUtil.applyPlaceholders(name, context),
                TextUtil.applyPlaceholders(value, context)
        );
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }
}
