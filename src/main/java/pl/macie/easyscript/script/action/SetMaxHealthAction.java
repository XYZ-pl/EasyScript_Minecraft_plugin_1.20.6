package pl.macie.easyscript.script.action;

import org.bukkit.entity.Player;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;

public final class SetMaxHealthAction implements Action {
    private final double maxHealth;
    private final SourceLocation source;

    public SetMaxHealthAction(double maxHealth, SourceLocation source) {
        this.maxHealth = Math.max(1.0, maxHealth);
        this.source = source;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void execute(ScriptContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return;
        }
        player.setMaxHealth(maxHealth);
        if (player.getHealth() > maxHealth) {
            player.setHealth(maxHealth);
        }
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }
}
