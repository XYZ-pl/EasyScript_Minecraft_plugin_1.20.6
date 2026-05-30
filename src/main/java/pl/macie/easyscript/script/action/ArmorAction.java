package pl.macie.easyscript.script.action;

import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import pl.macie.easyscript.script.SourceLocation;
import pl.macie.easyscript.script.model.ScriptContext;
import pl.macie.easyscript.util.ScriptItemUtil;

public final class ArmorAction implements Action {
    private final String slotName;
    private final String materialName;
    private final SourceLocation source;

    public ArmorAction(String slotName, String materialName, SourceLocation source) {
        this.slotName = slotName;
        this.materialName = materialName;
        this.source = source;
    }

    @Override
    public void execute(ScriptContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return;
        }
        EquipmentSlot slot = ScriptItemUtil.equipmentSlot(slotName);
        ItemStack item = ScriptItemUtil.item(materialName, 1, context);
        switch (slot) {
            case HEAD -> player.getInventory().setHelmet(item);
            case CHEST -> player.getInventory().setChestplate(item);
            case LEGS -> player.getInventory().setLeggings(item);
            case FEET -> player.getInventory().setBoots(item);
            case HAND -> player.getInventory().setItemInMainHand(item);
            case OFF_HAND -> player.getInventory().setItemInOffHand(item);
            default -> throw new IllegalArgumentException("Unsupported equipment slot: " + slotName);
        }
    }

    @Override
    public SourceLocation getSource() {
        return source;
    }
}
