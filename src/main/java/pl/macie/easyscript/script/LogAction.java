package pl.macie.easyscript.script.action;

import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.TextUtil;

public final class LogAction implements Action {
    private final String message;
    private final SourceLocation source;

    public LogAction(String message, SourceLocation source) {
        this.message = message;
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        context.getPlugin().getLogger().info(TextUtil.applyPlaceholders(message, context));
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }

    @Override
    public boolean canRunAsynchronously() {
        return true;
    }
}
