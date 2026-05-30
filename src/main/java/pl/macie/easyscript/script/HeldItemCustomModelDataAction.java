package pl.macie.easyscript.script.action;

import org.bukkit.inventory.meta.ItemMeta;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.ScriptItemUtil;

public final class HeldItemCustomModelDataAction implements Action {
    private final Integer modelData;
    private final SourceLocation source;

    public HeldItemCustomModelDataAction(Integer modelData, SourceLocation source) {
        this.modelData = modelData;
        this.source = source;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void execute(ScriptContext context) {
        ItemMeta meta = ScriptItemUtil.heldMeta(context);
        if (meta == null) {
            return;
        }
        meta.setCustomModelData(modelData);
        ScriptItemUtil.saveHeldMeta(context, meta);
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }
}
