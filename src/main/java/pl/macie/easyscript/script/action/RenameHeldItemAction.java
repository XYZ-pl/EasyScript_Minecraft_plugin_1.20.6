package pl.macie.easyscript.script.action;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.ColorUtil;
import pl.macie.easyscript.util.TextUtil;

public final class RenameHeldItemAction implements Action {
    private final String name;
    private final SourceLocation source;

    public RenameHeldItemAction(String name, SourceLocation source) {
        this.name = name;
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        ItemStack item = heldItem(context);
        if (item == null) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        meta.setDisplayName(ColorUtil.colorize(TextUtil.applyPlaceholders(name, context)));
        item.setItemMeta(meta);
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }

    private ItemStack heldItem(ScriptContext context) {
        Player player = context.getPlayer();
        return player == null ? null : player.getInventory().getItemInMainHand();
    }
}
