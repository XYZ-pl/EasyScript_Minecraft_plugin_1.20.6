package pl.macie.easyscript.script.model;

import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.action.Action;

import java.util.ArrayList;
import java.util.List;

public final class ScriptEvent {
    private final TriggerType triggerType;
    private final List<Action> actions = new ArrayList<>();
    private final SourceLocation source;

    public ScriptEvent(TriggerType triggerType, SourceLocation source) {
        this.triggerType = triggerType;
        this.source = source;
    }

    public TriggerType getTriggerType() {
        return triggerType;
    }

    public List<Action> getActions() {
        return List.copyOf(actions);
    }

    public SourceLocation getSource() {
        return source;
    }

    public void addAction(Action action) {
        actions.add(action);
    }
}
