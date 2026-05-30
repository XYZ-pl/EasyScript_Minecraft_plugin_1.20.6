package pl.macie.easyscript.script.action;

import org.bukkit.entity.Player;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;

public final class DamagePlayerAction implements Action {
    private final double amount;
    private final SourceLocation source;

    public DamagePlayerAction(double amount, SourceLocation source) {
        this.amount = Math.max(0.0, amount);
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        Player player = context.getPlayer();
        if (player != null) {
            player.damage(amount);
        }
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }
}
