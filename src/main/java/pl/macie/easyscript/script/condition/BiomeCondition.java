package pl.macie.easyscript.script.condition;

import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.TextUtil;

import java.util.Locale;

public final class BiomeCondition implements Condition {
    private final String biomeName;

    public BiomeCondition(String biomeName) {
        this.biomeName = biomeName;
    }

    @Override
    public boolean test(ScriptContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return false;
        }
        String prepared = TextUtil.applyPlaceholders(biomeName, context)
                .trim()
                .replace(' ', '_')
                .toUpperCase(Locale.ROOT);
        Biome expected = Biome.valueOf(prepared);
        return player.getLocation().getBlock().getBiome() == expected;
    }
}
