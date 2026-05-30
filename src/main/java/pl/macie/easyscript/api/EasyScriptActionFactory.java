package pl.macie.easyscript.api;

import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.action.Action;

import java.util.List;

@FunctionalInterface
public interface EasyScriptActionFactory {
    Action parse(String line, SourceLocation source, List<String> errors);
}
