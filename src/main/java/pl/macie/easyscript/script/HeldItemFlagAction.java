package pl.macie.easyscript.script.action;

import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.ScriptItemUtil;

public final class HeldItemFlagAction implements Action {
    private final String flag;
    private final boolean add;
    private final SourceLocation source;

    public HeldItemFlagAction(String flag, boolean add, SourceLocation source) {
        this.flag = flag;
        this.add = add;
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        ItemMeta meta = ScriptItemUtil.heldMeta(context);
        if (meta == null) {
            return;
        }
        ItemFlag itemFlag = ScriptItemUtil.itemFlag(flag, context);
        if (add) {
            meta.addItemFlags(itemFlag);
        } else {
            meta.removeItemFlags(itemFlag);
        }
        ScriptItemUtil.saveHeldMeta(context, meta);
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }
}
