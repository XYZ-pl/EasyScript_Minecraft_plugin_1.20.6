package pl.macie.easyscript.script.condition;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.ScriptItemUtil;

public final class HeldItemEnchantCondition implements Condition {
    private final String enchantmentName;
    private final int minimumLevel;

    public HeldItemEnchantCondition(String enchantmentName, int minimumLevel) {
        this.enchantmentName = enchantmentName;
        this.minimumLevel = Math.max(1, minimumLevel);
    }

    @Override
    public boolean test(ScriptContext context) {
        ItemStack item = ScriptItemUtil.heldItem(context);
        if (item == null) {
            return false;
        }
        Enchantment enchantment = ScriptItemUtil.enchantment(enchantmentName, context);
        return item.getEnchantmentLevel(enchantment) >= minimumLevel;
    }
}
