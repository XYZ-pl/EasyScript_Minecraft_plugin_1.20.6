package pl.macie.easyscript.script.action;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.TextUtil;

public final class RespawnPointAction implements Action {
    private final String target;
    private final SourceLocation source;

    public RespawnPointAction(String target, SourceLocation source) {
        this.target = target;
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return;
        }

        Location location = location(context, player);
        player.setBedSpawnLocation(location, true);
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }

    private Location location(ScriptContext context, Player player) {
        String prepared = TextUtil.applyPlaceholders(target, context).trim();
        if (prepared.equalsIgnoreCase("player") || prepared.equalsIgnoreCase("here")) {
            return player.getLocation();
        }
        if (prepared.equalsIgnoreCase("spawn")) {
            return player.getWorld().getSpawnLocation();
        }

        String[] parts = prepared.split(",");
        if (parts.length != 3 && parts.length != 4) {
            throw new IllegalArgumentException("Respawn point must be player, spawn, x,y,z or world,x,y,z");
        }

        int offset = parts.length == 4 ? 1 : 0;
        World world = player.getWorld();
        if (parts.length == 4) {
            world = Bukkit.getWorld(parts[0].trim());
            if (world == null) {
                throw new IllegalArgumentException("Unknown world: " + parts[0].trim());
            }
        }

        double x = Double.parseDouble(parts[offset].trim());
        double y = Double.parseDouble(parts[offset + 1].trim());
        double z = Double.parseDouble(parts[offset + 2].trim());
        return new Location(world, x, y, z);
    }
}
