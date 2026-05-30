package pl.macie.easyscript.script.model;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import pl.macie.easyscript.EasyScriptPlugin;
import pl.macie.easyscript.script.variable.ScriptVariables;
import pl.macie.easyscript.util.ScriptListUtil;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public final class ScriptContext {
    private final Plugin plugin;
    private final CommandSender sender;
    private final Player player;
    private final Event event;
    private final String commandLabel;
    private final String[] args;
    private final boolean asynchronous;
    private final Map<String, String> eventVariables;
    private final ScriptVariables scriptVariables;

    private ScriptContext(
            Plugin plugin,
            CommandSender sender,
            Player player,
            Event event,
            String commandLabel,
            String[] args,
            boolean asynchronous,
            Map<String, String> eventVariables,
            ScriptVariables scriptVariables
    ) {
        this.plugin = plugin;
        this.sender = sender;
        this.player = player;
        this.event = event;
        this.commandLabel = commandLabel;
        this.args = args == null ? new String[0] : args.clone();
        this.asynchronous = asynchronous;
        this.eventVariables = Map.copyOf(eventVariables);
        this.scriptVariables = scriptVariables == null ? ScriptVariables.disabled() : scriptVariables;
    }

    public static ScriptContext command(Plugin plugin, CommandSender sender, Player player, String commandLabel, String[] args) {
        Map<String, String> variables = new HashMap<>();
        variables.put("command", commandLabel);
        variables.put("args", String.join(" ", args));
        return new ScriptContext(plugin, sender, player, null, commandLabel, args, false, variables, resolveVariables(plugin));
    }

    public static ScriptContext playerEvent(Plugin plugin, Player player, Event event, boolean asynchronous) {
        return new ScriptContext(plugin, player, player, event, "", new String[0], asynchronous, Map.of(), resolveVariables(plugin));
    }

    public static ScriptContext chat(Plugin plugin, Player player, AsyncPlayerChatEvent event, boolean asynchronous) {
        Map<String, String> variables = new HashMap<>();
        variables.put("message", event.getMessage());
        return new ScriptContext(plugin, player, player, event, "", new String[0], asynchronous, variables, resolveVariables(plugin));
    }

    public static ScriptContext event(
            Plugin plugin,
            CommandSender sender,
            Player player,
            Event event,
            boolean asynchronous,
            Map<String, String> variables
    ) {
        return new ScriptContext(plugin, sender, player, event, "", new String[0], asynchronous, variables, resolveVariables(plugin));
    }

    public ScriptContext asSynchronous() {
        return new ScriptContext(plugin, sender, player, event, commandLabel, args, false, eventVariables, scriptVariables);
    }

    public ScriptContext withVariable(String name, String value) {
        Map<String, String> variables = new HashMap<>(eventVariables);
        variables.put(name.toLowerCase(Locale.ROOT), value);
        return new ScriptContext(plugin, sender, player, event, commandLabel, args, asynchronous, variables, scriptVariables);
    }

    public ScriptContext withPlayer(Player targetPlayer) {
        return new ScriptContext(plugin, sender, targetPlayer, event, commandLabel, args, asynchronous, eventVariables, scriptVariables);
    }

    public ScriptContext withVariables(Map<String, String> values) {
        Map<String, String> variables = new HashMap<>(eventVariables);
        for (Map.Entry<String, String> entry : values.entrySet()) {
            variables.put(entry.getKey().toLowerCase(Locale.ROOT), entry.getValue());
        }
        return new ScriptContext(plugin, sender, player, event, commandLabel, args, asynchronous, variables, scriptVariables);
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public CommandSender getSender() {
        return sender;
    }

    public Player getPlayer() {
        return player;
    }

    public Event getEvent() {
        return event;
    }

    public boolean isAsynchronous() {
        return asynchronous;
    }

    public ScriptVariables getVariables() {
        return scriptVariables;
    }

    public String placeholder(String name) {
        String normalized = name.toLowerCase(Locale.ROOT);
        if (eventVariables.containsKey(normalized)) {
            return eventVariables.get(normalized);
        }

        if (normalized.startsWith("arg-")) {
            return argument(normalized.substring(4));
        }
        if (normalized.startsWith("var:")) {
            return scriptVariables.get(normalized.substring("var:".length()));
        }
        if (normalized.startsWith("random-")) {
            return random(normalized.substring("random-".length()));
        }
        if (normalized.startsWith("list-size:")) {
            return String.valueOf(ScriptListUtil.size(scriptVariables, normalized.substring("list-size:".length())));
        }
        if (normalized.startsWith("list:")) {
            return ScriptListUtil.joined(scriptVariables, normalized.substring("list:".length()));
        }

        if (normalized.equals("player")) {
            return player == null ? senderName() : player.getName();
        }
        if (normalized.equals("display-name")) {
            return player == null ? senderName() : player.getDisplayName();
        }
        if (normalized.equals("sender")) {
            return senderName();
        }
        if (normalized.equals("uuid")) {
            UUID uuid = player == null ? null : player.getUniqueId();
            return uuid == null ? "" : uuid.toString();
        }
        if (normalized.equals("world")) {
            World world = player == null ? null : player.getWorld();
            return world == null ? "" : world.getName();
        }
        if (normalized.equals("x") || normalized.equals("y") || normalized.equals("z")) {
            return coordinate(normalized);
        }
        if (normalized.equals("yaw")) {
            return player == null ? "" : String.valueOf(Math.round(player.getLocation().getYaw() * 10.0F) / 10.0F);
        }
        if (normalized.equals("pitch")) {
            return player == null ? "" : String.valueOf(Math.round(player.getLocation().getPitch() * 10.0F) / 10.0F);
        }
        if (normalized.equals("biome")) {
            return player == null ? "" : player.getLocation().getBlock().getBiome().name().toLowerCase(Locale.ROOT);
        }
        if (normalized.equals("light")) {
            return player == null ? "" : String.valueOf(player.getLocation().getBlock().getLightLevel());
        }
        if (normalized.equals("health")) {
            return player == null ? "" : String.valueOf(Math.round(player.getHealth() * 10.0) / 10.0);
        }
        if (normalized.equals("max-health")) {
            return player == null ? "" : String.valueOf(Math.round(player.getMaxHealth() * 10.0) / 10.0);
        }
        if (normalized.equals("online")) {
            return String.valueOf(plugin.getServer().getOnlinePlayers().size());
        }
        if (normalized.equals("max-players")) {
            return String.valueOf(plugin.getServer().getMaxPlayers());
        }
        if (normalized.equals("player-list")) {
            return playerList();
        }
        if (normalized.equals("ping")) {
            return player == null ? "" : String.valueOf(player.getPing());
        }
        if (normalized.equals("ip")) {
            return player == null || player.getAddress() == null ? "" : player.getAddress().getAddress().getHostAddress();
        }
        if (normalized.equals("gamemode")) {
            return player == null ? "" : player.getGameMode().name().toLowerCase(Locale.ROOT);
        }
        if (normalized.equals("food")) {
            return player == null ? "" : String.valueOf(player.getFoodLevel());
        }
        if (normalized.equals("saturation")) {
            return player == null ? "" : String.valueOf(Math.round(player.getSaturation() * 10.0F) / 10.0F);
        }
        if (normalized.equals("exhaustion")) {
            return player == null ? "" : String.valueOf(Math.round(player.getExhaustion() * 10.0F) / 10.0F);
        }
        if (normalized.equals("level")) {
            return player == null ? "" : String.valueOf(player.getLevel());
        }
        if (normalized.equals("xp-progress")) {
            return player == null ? "" : String.valueOf(Math.round(player.getExp() * 100.0F) / 100.0F);
        }
        if (normalized.equals("weather")) {
            return weather();
        }
        if (normalized.equals("time")) {
            return player == null ? "" : String.valueOf(player.getWorld().getTime());
        }
        if (normalized.equals("world-time")) {
            return player == null ? "" : String.valueOf(player.getWorld().getFullTime());
        }
        if (normalized.equals("inventory-free")) {
            return player == null ? "" : String.valueOf(inventoryFreeSlots());
        }
        if (normalized.equals("held-item")) {
            return heldItemType();
        }
        if (normalized.equals("held-amount")) {
            return heldAmount();
        }
        if (normalized.equals("held-name")) {
            return heldName();
        }
        if (normalized.equals("is-op")) {
            return player == null ? "false" : String.valueOf(player.isOp());
        }
        if (normalized.equals("is-whitelisted")) {
            return player == null ? "false" : String.valueOf(player.isWhitelisted());
        }
        if (normalized.equals("is-flying")) {
            return player == null ? "false" : String.valueOf(player.isFlying());
        }
        if (normalized.equals("is-sneaking")) {
            return player == null ? "false" : String.valueOf(player.isSneaking());
        }
        if (normalized.equals("is-sprinting")) {
            return player == null ? "false" : String.valueOf(player.isSprinting());
        }
        if (normalized.equals("is-glowing")) {
            return player == null ? "false" : String.valueOf(player.isGlowing());
        }
        if (normalized.equals("is-invulnerable")) {
            return player == null ? "false" : String.valueOf(player.isInvulnerable());
        }
        if (normalized.equals("is-silent")) {
            return player == null ? "false" : String.valueOf(player.isSilent());
        }
        if (normalized.equals("has-gravity")) {
            return player == null ? "false" : String.valueOf(player.hasGravity());
        }
        if (normalized.equals("freeze-ticks")) {
            return player == null ? "" : String.valueOf(player.getFreezeTicks());
        }
        if (normalized.equals("air")) {
            return player == null ? "" : String.valueOf(player.getRemainingAir());
        }
        if (normalized.equals("max-air")) {
            return player == null ? "" : String.valueOf(player.getMaximumAir());
        }
        if (normalized.equals("arrows-in-body")) {
            return player == null ? "" : String.valueOf(player.getArrowsInBody());
        }
        if (normalized.equals("command")) {
            return commandLabel;
        }
        if (normalized.equals("args")) {
            return String.join(" ", args);
        }

        return "{" + name + "}";
    }

    private static ScriptVariables resolveVariables(Plugin plugin) {
        if (plugin instanceof EasyScriptPlugin easyScriptPlugin) {
            return easyScriptPlugin.getVariables();
        }
        return ScriptVariables.disabled();
    }

    private String argument(String numberText) {
        try {
            int index = Integer.parseInt(numberText) - 1;
            if (index < 0 || index >= args.length) {
                return "";
            }
            return args[index];
        } catch (NumberFormatException ignored) {
            return "";
        }
    }

    private String random(String rangeText) {
        String[] parts = rangeText.split("-", 2);
        if (parts.length != 2) {
            return "";
        }
        try {
            int min = Integer.parseInt(parts[0]);
            int max = Integer.parseInt(parts[1]);
            if (max < min) {
                int temp = min;
                min = max;
                max = temp;
            }
            int value = java.util.concurrent.ThreadLocalRandom.current().nextInt(min, max + 1);
            return String.valueOf(value);
        } catch (NumberFormatException ignored) {
            return "";
        }
    }

    private String coordinate(String axis) {
        if (player == null) {
            return "";
        }
        Location location = player.getLocation();
        double value = switch (axis) {
            case "x" -> location.getX();
            case "y" -> location.getY();
            case "z" -> location.getZ();
            default -> 0.0;
        };
        return String.valueOf(Math.round(value * 10.0) / 10.0);
    }

    private String playerList() {
        StringBuilder builder = new StringBuilder();
        for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
            if (!builder.isEmpty()) {
                builder.append(", ");
            }
            builder.append(onlinePlayer.getName());
        }
        return builder.toString();
    }

    private String weather() {
        if (player == null) {
            return "";
        }
        World world = player.getWorld();
        if (world.isThundering()) {
            return "thunder";
        }
        if (world.hasStorm()) {
            return "rain";
        }
        return "clear";
    }

    private int inventoryFreeSlots() {
        int freeSlots = 0;
        for (ItemStack item : player.getInventory().getStorageContents()) {
            if (item == null || item.getType().isAir()) {
                freeSlots++;
            }
        }
        return freeSlots;
    }

    private String heldItemType() {
        if (player == null) {
            return "";
        }
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType().isAir()) {
            return "air";
        }
        return item.getType().name().toLowerCase(Locale.ROOT);
    }

    private String heldAmount() {
        if (player == null) {
            return "";
        }
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType().isAir()) {
            return "0";
        }
        return String.valueOf(item.getAmount());
    }

    private String heldName() {
        if (player == null) {
            return "";
        }
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType().isAir()) {
            return "";
        }
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.hasDisplayName() ? meta.getDisplayName() : "";
    }

    private String senderName() {
        return sender == null ? "console" : sender.getName();
    }
}
