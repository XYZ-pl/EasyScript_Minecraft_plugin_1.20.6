package pl.macie.easyscript.script.action;

import org.bukkit.entity.Player;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;

public final class SetOperatorAction implements Action {
    private final boolean operator;
    private final SourceLocation source;

    public SetOperatorAction(boolean operator, SourceLocation source) {
        this.operator = operator;
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        Player player = context.getPlayer();
        if (player != null) {
            player.setOp(operator);
        }
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }
}
