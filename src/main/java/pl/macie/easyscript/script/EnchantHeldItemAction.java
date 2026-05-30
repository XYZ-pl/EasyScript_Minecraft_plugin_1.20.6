package pl.macie.easyscript.script.action;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.TextUtil;

import java.util.Locale;

public final class EnchantHeldItemAction implements Action {
    private final String enchantmentName;
    private final int level;
    private final SourceLocation source;

    public EnchantHeldItemAction(String enchantmentName, int level, SourceLocation source) {
        this.enchantmentName = enchantmentName;
        this.level = Math.max(1, level);
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType().isAir()) {
            return;
        }

        String prepared = TextUtil.applyPlaceholders(enchantmentName, context)
                .toLowerCase(Locale.ROOT)
                .replace("minecraft:", "")
                .replace(' ', '_');
        Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(prepared));
        if (enchantment == null) {
            enchantment = Enchantment.getByName(prepared.toUpperCase(Locale.ROOT));
        }
        if (enchantment == null) {
            throw new IllegalArgumentException("Unknown enchantment: " + prepared);
        }
        item.addUnsafeEnchantment(enchantment, level);
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }
}
