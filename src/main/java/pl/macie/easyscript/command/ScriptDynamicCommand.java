package pl.macie.easyscript.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import pl.macie.easyscript.script.ScriptManager;
import pl.macie.easyscript.script.model.ScriptCommand;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.ColorUtil;
import pl.macie.easyscript.util.TextUtil;
import pl.macie.easyscript.util.TimeUtil;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ScriptDynamicCommand extends Command {
    private final Plugin plugin;
    private final ScriptManager scriptManager;
    private final ScriptCommand scriptCommand;
    private final Map<String, Long> cooldowns = new ConcurrentHashMap<>();

    public ScriptDynamicCommand(Plugin plugin, ScriptManager scriptManager, ScriptCommand scriptCommand) {
        super(scriptCommand.getName(), scriptCommand.getDescription(), scriptCommand.getUsage(), scriptCommand.getAliases());
        this.plugin = plugin;
        this.scriptManager = scriptManager;
        this.scriptCommand = scriptCommand;
        setPermission(scriptCommand.getPermission());
        if (scriptCommand.getPermissionMessage() != null) {
            setPermissionMessage(ColorUtil.colorize(scriptCommand.getPermissionMessage()));
        }
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!testPermission(sender)) {
            return true;
        }

        Player player = sender instanceof Player ? (Player) sender : null;
        if (!validateArguments(sender, args)) {
            return true;
        }

        ScriptContext context = ScriptContext.command(plugin, sender, player, commandLabel, args);
        if (isOnCooldown(sender, player, context)) {
            return true;
        }

        scriptManager.executeActions(scriptCommand.getActions(), context);
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        return List.of();
    }

    private boolean isOnCooldown(CommandSender sender, Player player, ScriptContext context) {
        long cooldownTicks = scriptCommand.getCooldownTicks();
        if (cooldownTicks <= 0L || sender.hasPermission("easyscript.cooldown.bypass")) {
            return false;
        }

        String key = cooldownKey(sender, player);
        long now = System.currentTimeMillis();
        long until = cooldowns.getOrDefault(key, 0L);
        if (until > now) {
            long remainingTicks = Math.max(1L, Math.round((until - now) / 50.0));
            String message = scriptCommand.getCooldownMessage() == null
                    ? "&cWait &f{remaining}&c before using this command again."
                    : scriptCommand.getCooldownMessage();
            ScriptContext messageContext = context.withVariable("remaining", TimeUtil.formatShort(remainingTicks));
            sender.sendMessage(ColorUtil.colorize(TextUtil.applyPlaceholders(message, messageContext)));
            return true;
        }

        cooldowns.put(key, now + cooldownTicks * 50L);
        return false;
    }

    private String cooldownKey(CommandSender sender, Player player) {
        if (player != null) {
            UUID uuid = player.getUniqueId();
            return uuid.toString();
        }
        return "console:" + sender.getName().toLowerCase();
    }

    private boolean validateArguments(CommandSender sender, String[] args) {
        List<String> types = scriptCommand.getArgumentTypes();
        if (types.isEmpty()) {
            return true;
        }

        if (args.length < types.size()) {
            sender.sendMessage(ColorUtil.colorize("&cUsage: &f" + scriptCommand.getUsage()));
            return false;
        }

        for (int index = 0; index < types.size(); index++) {
            String type = types.get(index);
            if ((type.equals("number") || type.equals("integer")) && !isNumber(args[index], type.equals("integer"))) {
                sender.sendMessage(ColorUtil.colorize("&cArgument &f" + (index + 1) + " &cmust be a " + type + "."));
                return false;
            }
            if (type.equals("player") && plugin.getServer().getPlayerExact(args[index]) == null) {
                sender.sendMessage(ColorUtil.colorize("&cPlayer &f" + args[index] + " &cis not online."));
                return false;
            }
        }
        return true;
    }

    private boolean isNumber(String text, boolean integer) {
        try {
            if (integer) {
                Integer.parseInt(text);
            } else {
                Double.parseDouble(text.replace(',', '.'));
            }
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }
}
