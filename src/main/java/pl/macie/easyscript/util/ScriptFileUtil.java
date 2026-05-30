package pl.macie.easyscript.util;

import org.bukkit.plugin.Plugin;
import pl.macie.easyscript.EasyScriptPlugin;
import pl.macie.easyscript.script.model.ScriptContext;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ScriptFileUtil {
    private ScriptFileUtil() {
    }

    public static File resolve(ScriptContext context, String pathText) {
        String prepared = TextUtil.applyPlaceholders(pathText, context).trim();
        if (prepared.isBlank()) {
            throw new IllegalArgumentException("File path cannot be empty");
        }
        if (prepared.indexOf('\0') >= 0) {
            throw new IllegalArgumentException("File path contains an invalid character");
        }

        try {
            File root = rootFolder(context.getPlugin()).getCanonicalFile();
            if (!root.exists() && !root.mkdirs()) {
                throw new IllegalStateException("Could not create script files folder");
            }

            File target = new File(root, prepared).getCanonicalFile();
            Path rootPath = root.toPath();
            Path targetPath = target.toPath();
            if (!targetPath.startsWith(rootPath)) {
                throw new IllegalArgumentException("File path cannot leave the EasyScript files folder");
            }
            return target;
        } catch (IOException exception) {
            throw new IllegalStateException("Could not resolve file path", exception);
        }
    }

    public static String readLimited(ScriptContext context, File file) {
        int maxCharacters = maxReadCharacters(context.getPlugin());
        try {
            String text = Files.readString(file.toPath(), StandardCharsets.UTF_8);
            if (text.length() > maxCharacters) {
                return text.substring(0, maxCharacters);
            }
            return text;
        } catch (IOException exception) {
            throw new IllegalStateException("Could not read file: " + file.getName(), exception);
        }
    }

    public static String listFolder(File folder) {
        File[] files = folder.listFiles();
        if (files == null) {
            return "";
        }

        try (Stream<File> stream = Stream.of(files)) {
            return stream
                    .sorted(Comparator.comparing(File::getName, String.CASE_INSENSITIVE_ORDER))
                    .map(file -> file.getName() + (file.isDirectory() ? "/" : ""))
                    .collect(Collectors.joining(", "));
        }
    }

    public static void ensureParent(File file) {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IllegalStateException("Could not create parent folder for " + file.getName());
        }
    }

    private static File rootFolder(Plugin plugin) {
        if (plugin instanceof EasyScriptPlugin easyScriptPlugin) {
            return easyScriptPlugin.getScriptFilesFolder();
        }
        return new File(plugin.getDataFolder(), "files");
    }

    private static int maxReadCharacters(Plugin plugin) {
        if (plugin instanceof EasyScriptPlugin easyScriptPlugin) {
            return Math.max(1, easyScriptPlugin.getConfig().getInt("max-file-read-chars", 20000));
        }
        return 20000;
    }
}
