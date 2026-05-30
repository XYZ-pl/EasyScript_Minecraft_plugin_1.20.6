package pl.macie.easyscript.script.condition;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.ScriptItemUtil;

public final class HeldItemCondition implements Condition {
    private final String materialName;
    private final boolean air;

    public HeldItemCondition(String materialName, boolean air) {
        this.materialName = materialName;
        this.air = air;
    }

    public static HeldItemCondition air() {
        return new HeldItemCondition("", true);
    }

    @Override
    public boolean test(ScriptContext context) {
        ItemStack item = ScriptItemUtil.heldItem(context);
        if (air) {
            return item == null;
        }
        if (item == null) {
            return false;
        }
        Material material = ScriptItemUtil.material(materialName, context, true);
        return item.getType() == material;
    }
}
