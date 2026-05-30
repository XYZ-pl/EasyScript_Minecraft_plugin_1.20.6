package pl.macie.easyscript.util;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.macie.easyscript.script.model.ScriptContext;

import java.util.Locale;

public final class ScriptItemUtil {
    private ScriptItemUtil() {
    }

    public static Material material(String materialName, ScriptContext context, boolean itemOnly) {
        String prepared = TextUtil.applyPlaceholders(materialName, context)
                .trim()
                .replace("minecraft:", "")
                .replace(' ', '_')
                .toUpperCase(Locale.ROOT);
        Material material = Material.matchMaterial(prepared);
        if (material == null || (itemOnly && !material.isItem())) {
            throw new IllegalArgumentException("Unknown material: " + prepared);
        }
        return material;
    }

    public static ItemStack item(String materialName, int amount, ScriptContext context) {
        return new ItemStack(material(materialName, context, true), Math.max(1, amount));
    }

    public static ItemStack heldItem(ScriptContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return null;
        }
        ItemStack item = player.getInventory().getItemInMainHand();
        return item == null || item.getType().isAir() ? null : item;
    }

    public static ItemMeta heldMeta(ScriptContext context) {
        ItemStack item = heldItem(context);
        return item == null ? null : item.getItemMeta();
    }

    public static void saveHeldMeta(ScriptContext context, ItemMeta meta) {
        ItemStack item = heldItem(context);
        if (item != null && meta != null) {
            item.setItemMeta(meta);
        }
    }

    public static Enchantment enchantment(String enchantmentName, ScriptContext context) {
        String prepared = TextUtil.applyPlaceholders(enchantmentName, context)
                .trim()
                .replace("minecraft:", "")
                .replace(' ', '_')
                .toLowerCase(Locale.ROOT);
        Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(prepared));
        if (enchantment == null) {
            enchantment = Enchantment.getByName(prepared.toUpperCase(Locale.ROOT));
        }
        if (enchantment == null) {
            throw new IllegalArgumentException("Unknown enchantment: " + prepared);
        }
        return enchantment;
    }

    public static ItemFlag itemFlag(String flagName, ScriptContext context) {
        String prepared = TextUtil.applyPlaceholders(flagName, context)
                .trim()
                .replace(' ', '_')
                .toUpperCase(Locale.ROOT);
        return ItemFlag.valueOf(prepared);
    }

    public static EquipmentSlot equipmentSlot(String slotName) {
        return switch (slotName.toLowerCase(Locale.ROOT)) {
            case "helmet", "head" -> EquipmentSlot.HEAD;
            case "chestplate", "chest" -> EquipmentSlot.CHEST;
            case "leggings", "legs" -> EquipmentSlot.LEGS;
            case "boots", "feet" -> EquipmentSlot.FEET;
            case "mainhand", "hand" -> EquipmentSlot.HAND;
            case "offhand" -> EquipmentSlot.OFF_HAND;
            default -> throw new IllegalArgumentException("Unknown equipment slot: " + slotName);
        };
    }
}
