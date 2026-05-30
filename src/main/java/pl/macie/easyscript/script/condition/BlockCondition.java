package pl.macie.easyscript.script.condition;

import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.TextUtil;

public final class BlockCondition implements Condition {
    private final String materialName;

    public BlockCondition(String materialName) {
        this.materialName = materialName;
    }

    @Override
    public boolean test(ScriptContext context) {
        String block = context.placeholder("block-type");
        String expected = TextUtil.applyPlaceholders(materialName, context);
        return !block.isBlank() && block.equalsIgnoreCase(expected);
    }

    @Override
    public boolean canRunAsynchronously() {
        return true;
    }
}
