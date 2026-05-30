package pl.macie.easyscript.script.action;

import org.bukkit.inventory.ItemStack;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.ScriptItemUtil;

public final class HeldItemAmountAction implements Action {
    private final int amount;
    private final SourceLocation source;

    public HeldItemAmountAction(int amount, SourceLocation source) {
        this.amount = Math.max(0, amount);
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        ItemStack item = ScriptItemUtil.heldItem(context);
        if (item == null) {
            return;
        }
        if (amount <= 0) {
            item.setAmount(0);
            return;
        }
        item.setAmount(Math.min(amount, item.getMaxStackSize()));
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }
}
