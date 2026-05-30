package pl.macie.easyscript.script.action;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.TextUtil;

import java.util.Locale;

public final class PotionEffectAction implements Action {
    private final String effectName;
    private final int durationTicks;
    private final int amplifier;
    private final SourceLocation source;

    public PotionEffectAction(String effectName, int durationTicks, int amplifier, SourceLocation source) {
        this.effectName = effectName;
        this.durationTicks = Math.max(1, durationTicks);
        this.amplifier = Math.max(0, amplifier);
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return;
        }

        String preparedName = TextUtil.applyPlaceholders(effectName, context).toUpperCase(Locale.ROOT);
        PotionEffectType type = PotionEffectType.getByName(preparedName);
        if (type == null) {
            throw new IllegalArgumentException("Unknown potion effect: " + preparedName);
        }
        player.addPotionEffect(new PotionEffect(type, durationTicks, amplifier));
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }
}
