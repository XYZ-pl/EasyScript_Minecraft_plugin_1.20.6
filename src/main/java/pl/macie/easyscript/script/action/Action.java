package pl.macie.easyscript.script.action;

import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;

public interface Action {
    void execute(ScriptContext context);

    SourceLocation getSource();

    default boolean canRunAsynchronously() {
        return false;
    }
}
