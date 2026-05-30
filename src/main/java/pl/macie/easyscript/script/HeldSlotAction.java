package pl.macie.easyscript.script.action;

import org.bukkit.entity.Player;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;

public final class HeldSlotAction implements Action {
    private final int slot;
    private final SourceLocation source;

    public HeldSlotAction(int slot, SourceLocation source) {
        this.slot = Math.max(1, Math.min(9, slot));
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        Player player = context.getPlayer();
        if (player != null) {
            player.getInventory().setHeldItemSlot(slot - 1);
        }
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }
}
