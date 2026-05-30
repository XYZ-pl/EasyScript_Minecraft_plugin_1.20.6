package pl.macie.easyscript.script.action;

import org.bukkit.World;
import org.bukkit.entity.Player;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.TextUtil;

public final class WorldTimeAction implements Action {
    private final String time;
    private final SourceLocation source;

    public WorldTimeAction(String time, SourceLocation source) {
        this.time = time;
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        World world = world(context);
        String prepared = TextUtil.applyPlaceholders(time, context).trim();
        long ticks = switch (prepared.toLowerCase()) {
            case "day" -> 1000L;
            case "noon" -> 6000L;
            case "night" -> 13000L;
            case "midnight" -> 18000L;
            default -> Long.parseLong(prepared);
        };
        world.setTime(ticks);
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
}
