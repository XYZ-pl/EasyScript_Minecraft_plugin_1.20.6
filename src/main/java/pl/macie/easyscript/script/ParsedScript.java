package pl.macie.easyscript.script;

import pl.macie.easyscript.script.model.ScriptCommand;
import pl.macie.easyscript.script.model.ScriptEvent;
import pl.macie.easyscript.script.model.ScriptFunction;

import java.util.List;

public final class ParsedScript {
    private final List<ScriptCommand> commands;
    private final List<ScriptEvent> events;
    private final List<ScriptFunction> functions;
    private final List<String> errors;

    public ParsedScript(List<ScriptCommand> commands, List<ScriptEvent> events, List<ScriptFunction> functions, List<String> errors) {
        this.commands = List.copyOf(commands);
        this.events = List.copyOf(events);
        this.functions = List.copyOf(functions);
        this.errors = List.copyOf(errors);
    }

    public List<ScriptCommand> getCommands() {
        return commands;
    }

    public List<ScriptEvent> getEvents() {
        return events;
    }

    public List<ScriptFunction> getFunctions() {
        return functions;
    }

    public List<String> getErrors() {
        return errors;
    }
}
