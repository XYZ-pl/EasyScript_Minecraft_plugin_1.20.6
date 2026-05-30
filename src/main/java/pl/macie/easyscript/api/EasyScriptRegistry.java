package pl.macie.easyscript.api;

import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.action.Action;
import pl.macie.easyscript.script.condition.Condition;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class EasyScriptRegistry {
    private final Map<String, EasyScriptActionFactory> actions = new LinkedHashMap<>();
    private final Map<String, EasyScriptConditionFactory> conditions = new LinkedHashMap<>();

    public void registerAction(String prefix, EasyScriptActionFactory factory) {
        actions.put(normalize(prefix), factory);
    }

    public void registerCondition(String prefix, EasyScriptConditionFactory factory) {
        conditions.put(normalize(prefix), factory);
    }

    public Action parseAction(String line, SourceLocation source, List<String> errors) {
        String lower = line.toLowerCase(Locale.ROOT);
        for (Map.Entry<String, EasyScriptActionFactory> entry : actions.entrySet()) {
            if (lower.startsWith(entry.getKey())) {
                return entry.getValue().parse(line, source, errors);
            }
        }
        return null;
    }

    public Condition parseCondition(String condition, SourceLocation source, List<String> errors) {
        String lower = condition.toLowerCase(Locale.ROOT);
        for (Map.Entry<String, EasyScriptConditionFactory> entry : conditions.entrySet()) {
            if (lower.startsWith(entry.getKey())) {
                return entry.getValue().parse(condition, source, errors);
            }
        }
        return null;
    }

    public int actionCount() {
        return actions.size();
    }

    public int conditionCount() {
        return conditions.size();
    }

    private String normalize(String prefix) {
        return prefix == null ? "" : prefix.trim().toLowerCase(Locale.ROOT);
    }
}
