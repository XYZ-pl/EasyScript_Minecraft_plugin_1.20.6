package pl.macie.easyscript.script.model;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class RuntimeScripts {
    private final List<ScriptCommand> commands;
    private final Map<TriggerType, List<ScriptEvent>> events;
    private final Map<String, ScriptFunction> functions;

    public RuntimeScripts(List<ScriptCommand> commands, Map<TriggerType, List<ScriptEvent>> events, Map<String, ScriptFunction> functions) {
        this.commands = List.copyOf(commands);
        this.events = copyEvents(events);
        this.functions = Map.copyOf(functions);
    }

    public static RuntimeScripts empty() {
        Map<TriggerType, List<ScriptEvent>> events = new EnumMap<>(TriggerType.class);
        for (TriggerType triggerType : TriggerType.values()) {
            events.put(triggerType, List.of());
        }
        return new RuntimeScripts(List.of(), events, Map.of());
    }

    public List<ScriptCommand> getCommands() {
        return commands;
    }

    public List<ScriptEvent> getEvents(TriggerType triggerType) {
        return events.getOrDefault(triggerType, List.of());
    }

    public ScriptFunction getFunction(String name) {
        return functions.get(name.toLowerCase());
    }

    public java.util.Set<String> getFunctionNames() {
        return functions.keySet();
    }

    public int countEvents() {
        int count = 0;
        for (List<ScriptEvent> triggerEvents : events.values()) {
            count += triggerEvents.size();
        }
        return count;
    }

    public boolean hasEvents(TriggerType triggerType) {
        return !getEvents(triggerType).isEmpty();
    }

    private Map<TriggerType, List<ScriptEvent>> copyEvents(Map<TriggerType, List<ScriptEvent>> source) {
        Map<TriggerType, List<ScriptEvent>> copy = new EnumMap<>(TriggerType.class);
        for (TriggerType triggerType : TriggerType.values()) {
            copy.put(triggerType, List.copyOf(source.getOrDefault(triggerType, List.of())));
        }
        return copy;
    }
}
