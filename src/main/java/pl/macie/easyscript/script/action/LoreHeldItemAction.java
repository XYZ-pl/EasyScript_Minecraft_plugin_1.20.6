package pl.macie.easyscript.script.action;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.ColorUtil;
import pl.macie.easyscript.util.TextUtil;

import java.util.Arrays;

public final class LoreHeldItemAction implements Action {
    private final String lore;
    private final SourceLocation source;

    public LoreHeldItemAction(String lore, SourceLocation source) {
        this.lore = lore;
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
        meta.setLore(Arrays.stream(TextUtil.applyPlaceholders(lore, context).split("\\|", -1))
                .map(ColorUtil::colorize)
                .toList());
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
