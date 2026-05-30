package pl.macie.easyscript.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import pl.macie.easyscript.EasyScriptPlugin;
import pl.macie.easyscript.script.ScriptLoadResult;
import pl.macie.easyscript.script.ScriptManager;
import pl.macie.easyscript.util.ColorUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class EasyScriptAdminCommand implements CommandExecutor, TabCompleter {
    private static final List<String> SUBCOMMANDS = List.of("help", "reload", "list", "info", "debug", "enable", "disable");

    private final EasyScriptPlugin plugin;
    private final ScriptManager scriptManager;

    public EasyScriptAdminCommand(EasyScriptPlugin plugin, ScriptManager scriptManager) {
        this.plugin = plugin;
        this.scriptManager = scriptManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sendHelp(sender);
            return true;
        }

        String subcommand = args[0].toLowerCase(Locale.ROOT);
        switch (subcommand) {
            case "reload" -> reload(sender);
            case "list" -> list(sender);
            case "info" -> info(sender);
            case "debug" -> debug(sender);
            case "enable" -> toggle(sender, args, true);
            case "disable" -> toggle(sender, args, false);
            default -> {
                sender.sendMessage(ColorUtil.colorize("&cNieznana komenda. Uzyj &f/" + label + " help&c."));
                return true;
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 2 && (args[0].equalsIgnoreCase("enable") || args[0].equalsIgnoreCase("disable"))) {
            boolean includeDisabled = args[0].equalsIgnoreCase("enable");
            String prefix = args[1].toLowerCase(Locale.ROOT);
            List<String> matches = new ArrayList<>();
            for (String fileName : scriptManager.getScriptFileNames(true)) {
                if (!includeDisabled && fileName.startsWith("-")) {
                    continue;
                }
                if (includeDisabled && !fileName.startsWith("-")) {
                    continue;
                }
                if (fileName.toLowerCase(Locale.ROOT).startsWith(prefix)) {
                    matches.add(fileName);
                }
            }
            return matches;
        }

        if (args.length != 1) {
            return List.of();
        }

        String prefix = args[0].toLowerCase(Locale.ROOT);
        List<String> matches = new ArrayList<>();
        for (String subcommand : SUBCOMMANDS) {
            if (subcommand.startsWith(prefix)) {
                matches.add(subcommand);
            }
        }
        return matches;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ColorUtil.colorize("&8&m------------&r &aEasyScript &8&m------------"));
        sender.sendMessage(ColorUtil.colorize("&a/es reload &7- przeladowuje pliki .es"));
        sender.sendMessage(ColorUtil.colorize("&a/es list &7- pokazuje zaladowane skrypty i komendy"));
        sender.sendMessage(ColorUtil.colorize("&a/es info &7- pokazuje status silnika"));
        sender.sendMessage(ColorUtil.colorize("&a/es debug &7- pokazuje statystyki parsera i bledy"));
        sender.sendMessage(ColorUtil.colorize("&a/es disable <plik> &7- zmienia &fexample.es &7na &f-example.es"));
        sender.sendMessage(ColorUtil.colorize("&a/es enable <plik> &7- wlacza plik z prefixem &f-"));
    }

    private void reload(CommandSender sender) {
        ScriptLoadResult result = scriptManager.reloadScripts();
        sender.sendMessage(ColorUtil.colorize("&aPrzeladowano EasyScript: &f" + result.getCommandCount()
                + " komend&a, &f" + result.getEventCount() + " eventow&a, &f"
                + result.getLoadedFiles().size() + " plikow&a."));

        if (!result.getErrors().isEmpty()) {
            int maxErrors = Math.max(1, plugin.getConfig().getInt("max-visible-errors", 8));
            sender.sendMessage(ColorUtil.colorize("&eBledy podczas ladowania:"));
            result.getErrors().stream()
                    .limit(maxErrors)
                    .forEach(error -> sender.sendMessage(ColorUtil.colorize("&8- &c" + error)));
            if (result.getErrors().size() > maxErrors) {
                sender.sendMessage(ColorUtil.colorize("&7I jeszcze &f"
                        + (result.getErrors().size() - maxErrors) + " &7bledow w konsoli."));
            }
        }
    }

    private void list(CommandSender sender) {
        ScriptLoadResult result = scriptManager.getLastResult();
        sender.sendMessage(ColorUtil.colorize("&8&m------------&r &aEasyScript List &8&m------------"));
        sender.sendMessage(ColorUtil.colorize("&7Pliki: &f" + String.join("&7, &f", result.getLoadedFiles())));
        sender.sendMessage(ColorUtil.colorize("&7Wylaczone: &f" + String.join("&7, &f", result.getDisabledFiles())));
        sender.sendMessage(ColorUtil.colorize("&7Komendy: &f" + String.join("&7, &f", scriptManager.getLoadedCommandNames())));
        sender.sendMessage(ColorUtil.colorize("&7Eventy: &f" + result.getEventCount()));
        if (!result.getErrors().isEmpty()) {
            sender.sendMessage(ColorUtil.colorize("&eOstatnie ladowanie mialo &f" + result.getErrors().size() + " &ebledow."));
        }
    }

    private void info(CommandSender sender) {
        ScriptLoadResult result = scriptManager.getLastResult();
        sender.sendMessage(ColorUtil.colorize("&8&m------------&r &aEasyScript Info &8&m------------"));
        sender.sendMessage(ColorUtil.colorize("&7Wersja: &f" + plugin.getDescription().getVersion()));
        sender.sendMessage(ColorUtil.colorize("&7Folder: &f" + plugin.getScriptsFolder().getPath()));
        sender.sendMessage(ColorUtil.colorize("&7Rozszerzenie: &f.es"));
        sender.sendMessage(ColorUtil.colorize("&7Komendy: &f/easyscript &7i &f/es"));
        sender.sendMessage(ColorUtil.colorize("&7Kolory: &f& + kody legacy &7oraz &f&#RRGGBB"));
        sender.sendMessage(ColorUtil.colorize("&7Status: &f" + result.getCommandCount() + " komend, "
                + result.getEventCount() + " eventow"));
    }

    private void debug(CommandSender sender) {
        ScriptLoadResult result = scriptManager.getLastResult();
        sender.sendMessage(ColorUtil.colorize("&8&m------------&r &aEasyScript Debug &8&m------------"));
        sender.sendMessage(ColorUtil.colorize("&7Load time: &f" + result.getLoadTimeMillis() + "ms"));
        sender.sendMessage(ColorUtil.colorize("&7Loaded files: &f" + result.getLoadedFiles().size()));
        sender.sendMessage(ColorUtil.colorize("&7Disabled files: &f" + result.getDisabledFiles().size()));
        sender.sendMessage(ColorUtil.colorize("&7Commands: &f" + result.getCommandCount()));
        sender.sendMessage(ColorUtil.colorize("&7Events: &f" + result.getEventCount()));
        sender.sendMessage(ColorUtil.colorize("&7Functions: &f" + result.getFunctionCount()));
        sender.sendMessage(ColorUtil.colorize("&7Top-level actions: &f" + result.getActionCount()));
        sender.sendMessage(ColorUtil.colorize("&7Addon actions: &f" + scriptManager.getRegisteredAddonActionCount()));
        sender.sendMessage(ColorUtil.colorize("&7Addon conditions: &f" + scriptManager.getRegisteredAddonConditionCount()));
        sender.sendMessage(ColorUtil.colorize("&7Errors: &f" + result.getErrors().size()));
        result.getErrors().stream()
                .limit(5)
                .forEach(error -> sender.sendMessage(ColorUtil.colorize("&8- &c" + error)));
    }

    private void toggle(CommandSender sender, String[] args, boolean enable) {
        if (args.length < 2) {
            sender.sendMessage(ColorUtil.colorize("&cUzycie: &f/es " + (enable ? "enable" : "disable") + " <plik.es>"));
            return;
        }

        ScriptManager.ScriptToggleResult result = enable
                ? scriptManager.enableScript(args[1])
                : scriptManager.disableScript(args[1]);
        sender.sendMessage(ColorUtil.colorize((result.success() ? "&a" : "&c") + result.message()));
    }
}
