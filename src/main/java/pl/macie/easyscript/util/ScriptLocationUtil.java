package pl.macie.easyscript.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import pl.macie.easyscript.script.model.ScriptContext;

public final class ScriptLocationUtil {
    private ScriptLocationUtil() {
    }

    public static Location location(ScriptContext context, String target) {
        Player player = context.getPlayer();
        String prepared = TextUtil.applyPlaceholders(target, context).trim();
        if (prepared.equalsIgnoreCase("player") || prepared.equalsIgnoreCase("here")) {
            if (player == null) {
                throw new IllegalArgumentException("Player location is not available");
            }
            return player.getLocation();
        }

        if (prepared.equalsIgnoreCase("spawn")) {
            if (player == null) {
                World world = firstWorld(context);
                return world.getSpawnLocation();
            }
            return player.getWorld().getSpawnLocation();
        }

        String[] parts = prepared.split(",");
        if (parts.length != 3 && parts.length != 4 && parts.length != 5 && parts.length != 6) {
            throw new IllegalArgumentException("Location must be player, here, spawn, x,y,z or world,x,y,z");
        }

        int offset = parts.length >= 4 ? 1 : 0;
        World world = player == null ? firstWorld(context) : player.getWorld();
        if (parts.length >= 4) {
            world = Bukkit.getWorld(parts[0].trim());
            if (world == null) {
                throw new IllegalArgumentException("Unknown world: " + parts[0].trim());
            }
        }

        double x = Double.parseDouble(parts[offset].trim());
        double y = Double.parseDouble(parts[offset + 1].trim());
        double z = Double.parseDouble(parts[offset + 2].trim());
        Location location = new Location(world, x, y, z);
        if (parts.length - offset >= 5) {
            location.setYaw(Float.parseFloat(parts[offset + 3].trim()));
            location.setPitch(Float.parseFloat(parts[offset + 4].trim()));
        }
        return location;
    }

    public static Location blockLocation(ScriptContext context, String target) {
        Location location = location(context, target);
        return location.getBlock().getLocation();
    }

    public static World world(ScriptContext context, String target) {
        String prepared = TextUtil.applyPlaceholders(target, context).trim();
        if (prepared.equalsIgnoreCase("player") || prepared.equalsIgnoreCase("here")) {
            Player player = context.getPlayer();
            if (player == null) {
                throw new IllegalArgumentException("Player world is not available");
            }
            return player.getWorld();
        }
        World world = Bukkit.getWorld(prepared);
        if (world == null) {
            throw new IllegalArgumentException("Unknown world: " + prepared);
        }
        return world;
    }

    private static World firstWorld(ScriptContext context) {
        if (context.getPlugin().getServer().getWorlds().isEmpty()) {
            throw new IllegalArgumentException("No worlds are loaded");
        }
        return context.getPlugin().getServer().getWorlds().get(0);
    }
}
