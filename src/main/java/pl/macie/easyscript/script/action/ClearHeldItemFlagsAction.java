package pl.macie.easyscript.script.action;

import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.ScriptItemUtil;

public final class ClearHeldItemFlagsAction implements Action {
    private final SourceLocation source;

    public ClearHeldItemFlagsAction(SourceLocation source) {
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        ItemMeta meta = ScriptItemUtil.heldMeta(context);
        if (meta == null) {
            return;
        }
        meta.removeItemFlags(ItemFlag.values());
        ScriptItemUtil.saveHeldMeta(context, meta);
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }
}
