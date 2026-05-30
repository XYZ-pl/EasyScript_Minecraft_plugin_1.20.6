package pl.macie.easyscript.script.model;

import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.action.Action;

import java.util.List;

public final class ScriptFunction {
    private final String name;
    private final List<String> parameters;
    private final List<Action> actions;
    private final SourceLocation source;

    public ScriptFunction(String name, List<String> parameters, List<Action> actions, SourceLocation source) {
        this.name = name;
        this.parameters = List.copyOf(parameters);
        this.actions = List.copyOf(actions);
        this.source = source;
    }

    public String getName() {
        return name;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public List<Action> getActions() {
        return actions;
    }

    public SourceLocation getSource() {
        return source;
    }
}
