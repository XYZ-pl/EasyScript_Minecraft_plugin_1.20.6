package pl.macie.easyscript.script.condition;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.TextUtil;

import java.util.Locale;

public final class PotionEffectCondition implements Condition {
    private final String effectName;

    public PotionEffectCondition(String effectName) {
        this.effectName = effectName;
    }

    @Override
    public boolean test(ScriptContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return false;
        }
        String prepared = TextUtil.applyPlaceholders(effectName, context)
                .trim()
                .replace("minecraft:", "")
                .replace(' ', '_')
                .toUpperCase(Locale.ROOT);
        PotionEffectType type = PotionEffectType.getByName(prepared);
        return type != null && player.hasPotionEffect(type);
    }
}
