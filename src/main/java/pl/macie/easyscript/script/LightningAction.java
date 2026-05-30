package pl.macie.easyscript.script.action;

import org.bukkit.entity.Player;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;

public final class LightningAction implements Action {
    private final boolean effectOnly;
    private final SourceLocation source;

    public LightningAction(boolean effectOnly, SourceLocation source) {
        this.effectOnly = effectOnly;
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return;
        }

        if (effectOnly) {
            player.getWorld().strikeLightningEffect(player.getLocation());
        } else {
            player.getWorld().strikeLightning(player.getLocation());
        }
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }
}
