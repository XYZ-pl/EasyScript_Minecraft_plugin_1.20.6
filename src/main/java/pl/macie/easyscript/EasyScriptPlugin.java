package pl.macie.easyscript;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import pl.macie.easyscript.command.EasyScriptAdminCommand;
import pl.macie.easyscript.command.DynamicCommandRegistry;
import pl.macie.easyscript.script.ScriptLoadResult;
import pl.macie.easyscript.script.ScriptManager;
import pl.macie.easyscript.script.variable.ScriptVariables;

import java.io.File;

public final class EasyScriptPlugin extends JavaPlugin {
    private ScriptManager scriptManager;
    private DynamicCommandRegistry dynamicCommandRegistry;
    private ScriptVariables variables;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        ensureScriptsFolder();
        ensureScriptFilesFolder();
        saveExampleScript();

        variables = new ScriptVariables(new File(getDataFolder(), "variables.yml"));
        variables.load();
        startVariableAutosave();

        dynamicCommandRegistry = new DynamicCommandRegistry(this);
        scriptManager = new ScriptManager(this, dynamicCommandRegistry);

        EasyScriptAdminCommand adminCommand = new EasyScriptAdminCommand(this, scriptManager);
        PluginCommand command = getCommand("easyscript");
        if (command != null) {
            command.setExecutor(adminCommand);
            command.setTabCompleter(adminCommand);
        }

        Bukkit.getPluginManager().registerEvents(scriptManager, this);

        ScriptLoadResult result = scriptManager.reloadScripts();
        getLogger().info("Loaded " + result.getCommandCount() + " commands and "
                + result.getEventCount() + " event triggers from " + result.getLoadedFiles().size() + " scripts.");
        for (String error : result.getErrors()) {
            getLogger().warning(error);
        }
    }

    @Override
    public void onDisable() {
        if (scriptManager != null) {
            scriptManager.runUnloadEvents();
        }
        if (dynamicCommandRegistry != null) {
            dynamicCommandRegistry.unregisterAll();
        }
        if (variables != null) {
            variables.save();
        }
    }

    public ScriptVariables getVariables() {
        return variables;
    }

    public ScriptManager getScriptManager() {
        return scriptManager;
    }

    public File getScriptsFolder() {
        String folderName = getConfig().getString("scripts-folder", "scripts");
        return new File(getDataFolder(), folderName == null || folderName.isBlank() ? "scripts" : folderName);
    }

    public File getScriptFilesFolder() {
        String folderName = getConfig().getString("script-files-folder", "files");
        return new File(getDataFolder(), folderName == null || folderName.isBlank() ? "files" : folderName);
    }

    private void ensureScriptsFolder() {
        File folder = getScriptsFolder();
        if (!folder.exists() && !folder.mkdirs()) {
            getLogger().warning("Could not create scripts folder: " + folder.getAbsolutePath());
        }
    }

    private void ensureScriptFilesFolder() {
        File folder = getScriptFilesFolder();
        if (!folder.exists() && !folder.mkdirs()) {
            getLogger().warning("Could not create script files folder: " + folder.getAbsolutePath());
        }
    }

    private void saveExampleScript() {
        if (!getConfig().getBoolean("save-example-script", true)) {
            return;
        }

        File example = new File(getScriptsFolder(), "example.es");
        if (!example.exists()) {
            saveResource("scripts/example.es", false);
        }
    }

    private void startVariableAutosave() {
        long minutes = getConfig().getLong("variables-autosave-minutes", 5L);
        if (minutes <= 0L) {
            return;
        }

        long periodTicks = Math.max(20L, minutes * 60L * 20L);
        getServer().getScheduler().runTaskTimer(this, () -> {
            if (variables != null) {
                variables.save();
            }
        }, periodTicks, periodTicks);
    }
}
