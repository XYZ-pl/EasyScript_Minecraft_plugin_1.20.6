package pl.macie.easyscript.script.action;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;

public final class HealAction implements Action {
    private final Double amount;
    private final SourceLocation source;

    public HealAction(Double amount, SourceLocation source) {
        this.amount = amount;
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return;
        }

        AttributeInstance maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        double maximum = maxHealth == null ? 20.0 : maxHealth.getValue();
        double target = amount == null ? maximum : player.getHealth() + amount;
        player.setHealth(Math.max(0.0, Math.min(maximum, target)));
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }
}
