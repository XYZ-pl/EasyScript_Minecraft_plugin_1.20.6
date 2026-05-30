package pl.macie.easyscript.script.action;

import org.bukkit.entity.Player;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;

public final class SetHealthAction implements Action {
    private final double health;
    private final SourceLocation source;

    public SetHealthAction(double health, SourceLocation source) {
        this.health = Math.max(0.0, health);
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return;
        }
        player.setHealth(Math.min(health, player.getMaxHealth()));
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }
}
