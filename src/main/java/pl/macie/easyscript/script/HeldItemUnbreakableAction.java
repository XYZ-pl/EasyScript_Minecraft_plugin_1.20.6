package pl.macie.easyscript.script.action;

import org.bukkit.inventory.meta.ItemMeta;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.ScriptItemUtil;

public final class HeldItemUnbreakableAction implements Action {
    private final boolean unbreakable;
    private final SourceLocation source;

    public HeldItemUnbreakableAction(boolean unbreakable, SourceLocation source) {
        this.unbreakable = unbreakable;
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        ItemMeta meta = ScriptItemUtil.heldMeta(context);
        if (meta == null) {
            return;
        }
        meta.setUnbreakable(unbreakable);
        ScriptItemUtil.saveHeldMeta(context, meta);
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }
}
