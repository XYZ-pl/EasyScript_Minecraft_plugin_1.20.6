package pl.macie.easyscript.api;

import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.condition.Condition;

import java.util.List;

@FunctionalInterface
public interface EasyScriptConditionFactory {
    Condition parse(String condition, SourceLocation source, List<String> errors);
}
