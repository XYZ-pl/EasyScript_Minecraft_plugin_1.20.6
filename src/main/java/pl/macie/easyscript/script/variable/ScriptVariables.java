package pl.macie.easyscript.script.variable;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ScriptVariables {
    private static final ScriptVariables DISABLED = new ScriptVariables(null);

    private final File file;
    private final Map<String, String> values = new ConcurrentHashMap<>();
    private volatile boolean dirty;

    public ScriptVariables(File file) {
        this.file = file;
    }

    public static ScriptVariables disabled() {
        return DISABLED;
    }

    public void load() {
        if (file == null || !file.exists()) {
            return;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = config.getConfigurationSection("variables");
        if (section == null) {
            return;
        }

        values.clear();
        for (String key : section.getKeys(false)) {
            values.put(normalize(key), section.getString(key, ""));
        }
        dirty = false;
    }

    public void save() {
        if (file == null || !dirty) {
            return;
        }

        YamlConfiguration config = new YamlConfiguration();
        for (Map.Entry<String, String> entry : values.entrySet()) {
            config.set("variables." + entry.getKey(), entry.getValue());
        }

        try {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            config.save(file);
            dirty = false;
        } catch (IOException exception) {
            throw new IllegalStateException("Could not save EasyScript variables", exception);
        }
    }

    public String get(String key) {
        return values.getOrDefault(normalize(key), "");
    }

    public void set(String key, String value) {
        values.put(normalize(key), value == null ? "" : value);
        dirty = true;
    }

    public void delete(String key) {
        values.remove(normalize(key));
        dirty = true;
    }

    private String normalize(String key) {
        return key == null ? "" : key.trim().toLowerCase(Locale.ROOT);
    }
}
