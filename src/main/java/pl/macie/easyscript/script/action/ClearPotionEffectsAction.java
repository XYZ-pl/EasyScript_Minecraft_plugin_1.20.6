package pl.macie.easyscript.script.action;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;

import java.util.ArrayList;

public final class ClearPotionEffectsAction implements Action {
    private final SourceLocation source;

    public ClearPotionEffectsAction(SourceLocation source) {
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return;
        }
        for (PotionEffect effect : new ArrayList<>(player.getActivePotionEffects())) {
            player.removePotionEffect(effect.getType());
        }
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }
}
