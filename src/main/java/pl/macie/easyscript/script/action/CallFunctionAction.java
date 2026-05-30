package pl.macie.easyscript.script.action;

import pl.macie.easyscript.EasyScriptPlugin;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.TextUtil;

import java.util.ArrayList;
import java.util.List;

public final class CallFunctionAction implements Action {
    private final String functionName;
    private final List<String> arguments;
    private final SourceLocation source;

    public CallFunctionAction(String functionName, List<String> arguments, SourceLocation source) {
        this.functionName = functionName;
        this.arguments = List.copyOf(arguments);
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        if (!(context.getPlugin() instanceof EasyScriptPlugin plugin)) {
            return;
        }

        List<String> preparedArguments = new ArrayList<>();
        for (String argument : arguments) {
            preparedArguments.add(TextUtil.applyPlaceholders(argument, context));
        }
        plugin.getScriptManager().callFunction(functionName, preparedArguments, context);
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }
}
