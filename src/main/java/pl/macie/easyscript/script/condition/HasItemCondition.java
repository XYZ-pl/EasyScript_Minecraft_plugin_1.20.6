package pl.macie.easyscript.script.condition;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.TextUtil;

import java.util.Locale;

public final class HasItemCondition implements Condition {
    private final String materialName;
    private final int amount;

    public HasItemCondition(String materialName, int amount) {
        this.materialName = materialName;
        this.amount = Math.max(1, amount);
    }

    @Override
    public boolean test(ScriptContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return false;
        }

        String prepared = TextUtil.applyPlaceholders(materialName, context).toUpperCase(Locale.ROOT);
        Material material = Material.matchMaterial(prepared);
        return material != null && player.getInventory().containsAtLeast(new org.bukkit.inventory.ItemStack(material), amount);
    }
}
