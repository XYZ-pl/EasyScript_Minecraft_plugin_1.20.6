package pl.macie.easyscript.script.action;

import org.bukkit.entity.Player;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;

public final class ExhaustionAction implements Action {
    private final float exhaustion;
    private final SourceLocation source;

    public ExhaustionAction(float exhaustion, SourceLocation source) {
        this.exhaustion = Math.max(0.0F, exhaustion);
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        Player player = context.getPlayer();
        if (player != null) {
            player.setExhaustion(exhaustion);
        }
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }
}
