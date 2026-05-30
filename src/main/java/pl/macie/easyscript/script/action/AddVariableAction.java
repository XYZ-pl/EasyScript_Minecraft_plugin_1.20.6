package pl.macie.easyscript.script.action;

import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.TextUtil;

public class AddVariableAction implements Action {
    private final String name;
    private final String amount;
    private final SourceLocation source;

    public AddVariableAction(String name, String amount, SourceLocation source) {
        this.name = name;
        this.amount = amount;
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        String variableName = TextUtil.applyPlaceholders(name, context);
        double current = number(context.getVariables().get(variableName));
        double delta = number(TextUtil.applyPlaceholders(amount, context));
        context.getVariables().set(variableName, clean(current + delta));
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }

    private double number(String value) {
        try {
            return Double.parseDouble(value.replace(',', '.'));
        } catch (NumberFormatException ignored) {
            return 0.0;
        }
    }

    private String clean(double value) {
        if (value == Math.rint(value)) {
            return String.valueOf((long) value);
        }
        return String.valueOf(value);
    }
}
