package pl.macie.easyscript.script.action;

import org.bukkit.entity.Player;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;

public final class CloseInventoryAction implements Action {
    private final SourceLocation source;

    public CloseInventoryAction(SourceLocation source) {
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        Player player = context.getPlayer();
        if (player != null) {
            player.closeInventory();
        }
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }
}
