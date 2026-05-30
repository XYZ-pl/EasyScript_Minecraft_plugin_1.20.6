package pl.macie.easyscript.script.condition;

import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.TextUtil;

import java.util.Locale;

public final class ComparisonCondition implements Condition {
    private final String left;
    private final Operator operator;
    private final String right;

    public ComparisonCondition(String left, Operator operator, String right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public boolean test(ScriptContext context) {
        String preparedLeft = TextUtil.applyPlaceholders(left, context);
        String preparedRight = TextUtil.applyPlaceholders(right, context);
        Double leftNumber = number(preparedLeft);
        Double rightNumber = number(preparedRight);

        if (leftNumber != null && rightNumber != null) {
            int compared = Double.compare(leftNumber, rightNumber);
            return switch (operator) {
                case EQUALS -> compared == 0;
                case NOT_EQUALS -> compared != 0;
                case GREATER -> compared > 0;
                case GREATER_OR_EQUAL -> compared >= 0;
                case LESS -> compared < 0;
                case LESS_OR_EQUAL -> compared <= 0;
                case CONTAINS -> preparedLeft.contains(preparedRight);
            };
        }

        int compared = preparedLeft.compareToIgnoreCase(preparedRight);
        return switch (operator) {
            case EQUALS -> preparedLeft.equalsIgnoreCase(preparedRight);
            case NOT_EQUALS -> !preparedLeft.equalsIgnoreCase(preparedRight);
            case GREATER -> compared > 0;
            case GREATER_OR_EQUAL -> compared >= 0;
            case LESS -> compared < 0;
            case LESS_OR_EQUAL -> compared <= 0;
            case CONTAINS -> preparedLeft.toLowerCase(Locale.ROOT).contains(preparedRight.toLowerCase(Locale.ROOT));
        };
    }

    @Override
    public boolean canRunAsynchronously() {
        return true;
    }

    private Double number(String text) {
        try {
            return Double.parseDouble(text.replace(',', '.'));
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    public enum Operator {
        EQUALS,
        NOT_EQUALS,
        GREATER,
        GREATER_OR_EQUAL,
        LESS,
        LESS_OR_EQUAL,
        CONTAINS
    }
}
