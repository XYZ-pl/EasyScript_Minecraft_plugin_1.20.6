package pl.macie.easyscript.script;

import java.util.List;

public final class ScriptLoadResult {
    private final List<String> loadedFiles;
    private final List<String> disabledFiles;
    private final int commandCount;
    private final int eventCount;
    private final int functionCount;
    private final int actionCount;
    private final long loadTimeMillis;
    private final List<String> errors;

    public ScriptLoadResult(List<String> loadedFiles, int commandCount, int eventCount, List<String> errors) {
        this(loadedFiles, List.of(), commandCount, eventCount, 0, 0, 0L, errors);
    }

    public ScriptLoadResult(
            List<String> loadedFiles,
            List<String> disabledFiles,
            int commandCount,
            int eventCount,
            int functionCount,
            int actionCount,
            long loadTimeMillis,
            List<String> errors
    ) {
        this.loadedFiles = List.copyOf(loadedFiles);
        this.disabledFiles = List.copyOf(disabledFiles);
        this.commandCount = commandCount;
        this.eventCount = eventCount;
        this.functionCount = functionCount;
        this.actionCount = actionCount;
        this.loadTimeMillis = loadTimeMillis;
        this.errors = List.copyOf(errors);
    }

    public static ScriptLoadResult empty() {
        return new ScriptLoadResult(List.of(), 0, 0, List.of());
    }

    public static ScriptLoadResult failure(List<String> errors) {
        return new ScriptLoadResult(List.of(), 0, 0, errors);
    }

    public List<String> getLoadedFiles() {
        return loadedFiles;
    }

    public List<String> getDisabledFiles() {
        return disabledFiles;
    }

    public int getCommandCount() {
        return commandCount;
    }

    public int getEventCount() {
        return eventCount;
    }

    public int getFunctionCount() {
        return functionCount;
    }

    public int getActionCount() {
        return actionCount;
    }

    public long getLoadTimeMillis() {
        return loadTimeMillis;
    }

    public List<String> getErrors() {
        return errors;
    }
}
