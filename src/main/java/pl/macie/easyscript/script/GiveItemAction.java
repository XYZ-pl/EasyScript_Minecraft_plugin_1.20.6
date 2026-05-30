package pl.macie.easyscript.script.action;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.TextUtil;

import java.util.Locale;

public final class GiveItemAction implements Action {
    private final String materialName;
    private final int amount;
    private final SourceLocation source;

    public GiveItemAction(String materialName, int amount, SourceLocation source) {
        this.materialName = materialName;
        this.amount = Math.max(1, amount);
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return;
        }

        Material material = material(context);
        player.getInventory().addItem(new ItemStack(material, amount));
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }

    private Material material(ScriptContext context) {
        String prepared = TextUtil.applyPlaceholders(materialName, context).toUpperCase(Locale.ROOT);
        Material material = Material.matchMaterial(prepared);
        if (material == null || !material.isItem()) {
            throw new IllegalArgumentException("Unknown item material: " + prepared);
        }
        return material;
    }
}
