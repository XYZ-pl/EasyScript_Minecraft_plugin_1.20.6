package pl.macie.easyscript.script.action;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.ScriptItemUtil;
import pl.macie.easyscript.util.ScriptLocationUtil;

public final class DropItemAction implements Action {
    private final String materialName;
    private final int amount;
    private final String target;
    private final SourceLocation source;

    public DropItemAction(String materialName, int amount, String target, SourceLocation source) {
        this.materialName = materialName;
        this.amount = Math.max(1, amount);
        this.target = target;
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        Location location = ScriptLocationUtil.location(context, target);
        ItemStack item = ScriptItemUtil.item(materialName, amount, context);
        location.getWorld().dropItemNaturally(location, item);
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }
}
