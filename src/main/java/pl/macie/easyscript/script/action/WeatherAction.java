package pl.macie.easyscript.script.action;

import org.bukkit.World;
import org.bukkit.entity.Player;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;

public final class WeatherAction implements Action {
    private final Mode mode;
    private final SourceLocation source;

    public WeatherAction(Mode mode, SourceLocation source) {
        this.mode = mode;
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        World world = world(context);
        switch (mode) {
            case CLEAR -> {
                world.setStorm(false);
                world.setThundering(false);
            }
            case RAIN -> {
                world.setStorm(true);
                world.setThundering(false);
            }
            case THUNDER -> {
                world.setStorm(true);
                world.setThundering(true);
            }
        }
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }

    private World world(ScriptContext context) {
        Player player = context.getPlayer();
        if (player != null) {
            return player.getWorld();
        }
        return context.getPlugin().getServer().getWorlds().get(0);
    }

    public enum Mode {
        CLEAR,
        RAIN,
        THUNDER
    }
}
