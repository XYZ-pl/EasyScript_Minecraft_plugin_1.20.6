package pl.macie.easyscript.script.action;

import org.bukkit.entity.Player;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;

public final class OpenInventoryAction implements Action {
    private final Type type;
    private final SourceLocation source;

    public OpenInventoryAction(Type type, SourceLocation source) {
        this.type = type;
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return;
        }

        switch (type) {
            case ENDER_CHEST -> player.openInventory(player.getEnderChest());
            case WORKBENCH -> player.openWorkbench(null, true);
            case ENCHANTING -> player.openEnchanting(null, true);
            case PLAYER_INVENTORY -> player.openInventory(player.getInventory());
        }
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }

    public enum Type {
        ENDER_CHEST,
        WORKBENCH,
        ENCHANTING,
        PLAYER_INVENTORY
    }
}
