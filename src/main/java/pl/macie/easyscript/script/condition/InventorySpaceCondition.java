package pl.macie.easyscript.script.condition;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.macie.easyscript.script.model.ScriptContext;

public final class InventorySpaceCondition implements Condition {
    private final Mode mode;

    public InventorySpaceCondition(Mode mode) {
        this.mode = mode;
    }

    @Override
    public boolean test(ScriptContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return false;
        }
        boolean hasSpace = player.getInventory().firstEmpty() >= 0;
        boolean empty = true;
        for (ItemStack item : player.getInventory().getStorageContents()) {
            if (item != null && !item.getType().isAir()) {
                empty = false;
                break;
            }
        }

        return switch (mode) {
            case HAS_SPACE -> hasSpace;
            case FULL -> !hasSpace;
            case EMPTY -> empty;
        };
    }

    public enum Mode {
        HAS_SPACE,
        FULL,
        EMPTY
    }
}
