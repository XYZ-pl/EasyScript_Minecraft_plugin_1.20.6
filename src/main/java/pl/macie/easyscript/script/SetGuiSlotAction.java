package pl.macie.easyscript.script.action;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.ColorUtil;
import pl.macie.easyscript.util.ScriptItemUtil;
import pl.macie.easyscript.util.TextUtil;

import java.util.Arrays;

public final class SetGuiSlotAction implements Action {
    private final int slot;
    private final String materialName;
    private final String name;
    private final String lore;
    private final SourceLocation source;

    public SetGuiSlotAction(int slot, String materialName, String name, String lore, SourceLocation source) {
        this.slot = Math.max(0, slot);
        this.materialName = materialName;
        this.name = name;
        this.lore = lore;
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return;
        }
        Inventory inventory = player.getOpenInventory().getTopInventory();
        if (slot >= inventory.getSize()) {
            return;
        }

        ItemStack item = ScriptItemUtil.item(materialName, 1, context);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            if (name != null && !name.isBlank()) {
                meta.setDisplayName(ColorUtil.colorize(TextUtil.applyPlaceholders(name, context)));
            }
            if (lore != null && !lore.isBlank()) {
                meta.setLore(Arrays.stream(TextUtil.applyPlaceholders(lore, context).split("\\|", -1))
                        .map(ColorUtil::colorize)
                        .toList());
            }
            item.setItemMeta(meta);
        }
        inventory.setItem(slot, item);
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }
}
