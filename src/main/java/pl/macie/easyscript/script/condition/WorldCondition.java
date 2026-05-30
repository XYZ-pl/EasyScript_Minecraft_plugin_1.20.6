package pl.macie.easyscript.script.condition;

import org.bukkit.entity.Player;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.TextUtil;

public final class WorldCondition implements Condition {
    private final String world;

    public WorldCondition(String world) {
        this.world = world;
    }

    @Override
    public boolean test(ScriptContext context) {
        Player player = context.getPlayer();
        return player != null && player.getWorld().getName().equalsIgnoreCase(TextUtil.applyPlaceholders(world, context));
    }
}
