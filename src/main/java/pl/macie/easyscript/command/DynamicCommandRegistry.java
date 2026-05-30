package pl.macie.easyscript.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.Plugin;
import pl.macie.easyscript.script.ScriptManager;
import pl.macie.easyscript.script.model.ScriptCommand;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class DynamicCommandRegistry {
    private final Plugin plugin;
    private final List<Command> registeredCommands = new ArrayList<>();

    public DynamicCommandRegistry(Plugin plugin) {
        this.plugin = plugin;
    }

    public void registerAll(List<ScriptCommand> commands, ScriptManager scriptManager) {
        unregisterAll();
        CommandMap commandMap = getCommandMap();
        if (commandMap == null) {
            plugin.getLogger().warning("Could not access Bukkit command map. Script commands were not registered.");
            return;
        }

        for (ScriptCommand scriptCommand : commands) {
            ScriptDynamicCommand command = new ScriptDynamicCommand(plugin, scriptManager, scriptCommand);
            commandMap.register(plugin.getName().toLowerCase(Locale.ROOT), command);
            registeredCommands.add(command);
        }
    }

    public void unregisterAll() {
        CommandMap commandMap = getCommandMap();
        if (commandMap == null) {
            registeredCommands.clear();
            return;
        }

        for (Command command : registeredCommands) {
            command.unregister(commandMap);
            removeKnownCommand(commandMap, command);
        }
        registeredCommands.clear();
    }

    private CommandMap getCommandMap() {
        try {
            Method method = Bukkit.getServer().getClass().getMethod("getCommandMap");
            return (CommandMap) method.invoke(Bukkit.getServer());
        } catch (ReflectiveOperationException exception) {
            plugin.getLogger().warning("Could not read command map: " + exception.getMessage());
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private void removeKnownCommand(CommandMap commandMap, Command command) {
        if (!(commandMap instanceof SimpleCommandMap)) {
            return;
        }

        try {
            Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
            knownCommandsField.setAccessible(true);
            Map<String, Command> knownCommands = (Map<String, Command>) knownCommandsField.get(commandMap);
            knownCommands.entrySet().removeIf(entry -> entry.getValue() == command);
        } catch (ReflectiveOperationException exception) {
            plugin.getLogger().warning("Could not fully unregister command " + command.getName() + ": "
                    + exception.getMessage());
        }
    }
}
