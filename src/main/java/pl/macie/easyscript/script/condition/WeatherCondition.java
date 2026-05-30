package pl.macie.easyscript.script.condition;

import org.bukkit.World;
import org.bukkit.entity.Player;
import pl.macie.easyscript.script.model.ScriptContext;

public final class WeatherCondition implements Condition {
    private final Mode mode;

    public WeatherCondition(Mode mode) {
        this.mode = mode;
    }

    @Override
    public boolean test(ScriptContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return false;
        }
        World world = player.getWorld();
        return switch (mode) {
            case CLEAR -> !world.hasStorm() && !world.isThundering();
            case RAIN -> world.hasStorm() && !world.isThundering();
            case THUNDER -> world.isThundering();
        };
    }

    public enum Mode {
        CLEAR,
        RAIN,
        THUNDER
    }
}
