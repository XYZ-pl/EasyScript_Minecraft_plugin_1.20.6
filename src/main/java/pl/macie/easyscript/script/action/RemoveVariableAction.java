package pl.macie.easyscript.script.action;

import pl.macie.easyscript.script.SourceLocation;

public final class RemoveVariableAction extends AddVariableAction {
    public RemoveVariableAction(String name, String amount, SourceLocation source) {
        super(name, "-" + amount, source);
    }
}
