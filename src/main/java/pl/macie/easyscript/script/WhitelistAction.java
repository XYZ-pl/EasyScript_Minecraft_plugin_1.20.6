package pl.macie.easyscript.script.action;

import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;

public final class WhitelistAction implements Action {
    private final boolean enabled;
    private final SourceLocation source;

    public WhitelistAction(boolean enabled, SourceLocation source) {
        this.enabled = enabled;
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        context.getPlugin().getServer().setWhitelist(enabled);
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }
}
