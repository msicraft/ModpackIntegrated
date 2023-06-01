package me.msicraft.modpackintegrated.Util;

import org.bukkit.EntityEffect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerUtil {

    public static int getPlayerEmptySlot(Player player) {
        int slot = -1;
        int size = 36;
        for (int a = 0; a<size; a++) {
            ItemStack itemStack = player.getInventory().getItem(a);
            if (itemStack == null) {
                slot = a;
                break;
            }

        }
        return slot;
    }

    public static int hasTotemOfUndying(Player player) {
        int slot = -1;
        for (int a = 0; a<36; a++) {
            ItemStack itemStack = player.getInventory().getItem(a);
            if (itemStack != null && itemStack.getType() == Material.TOTEM_OF_UNDYING) {
                slot = a;
                break;
            }
        }
        return slot;
    }

    private static final PotionEffect regenerationEffect = new PotionEffect(PotionEffectType.REGENERATION, (20*45), 1, false, false);
    private static final PotionEffect absorptionEffect = new PotionEffect(PotionEffectType.ABSORPTION, (20*5), 1, false, false);

    public static void applyTotemOfUndying(Player player, int slot) {
        ItemStack itemStack = player.getInventory().getItem(slot);
        if (itemStack != null && itemStack.getType() == Material.TOTEM_OF_UNDYING) {
            itemStack.setAmount(itemStack.getAmount() - 1);
            player.playEffect(EntityEffect.TOTEM_RESURRECT);
            player.playSound(player.getLocation(), Sound.ITEM_TOTEM_USE, 1, 1);
            double maxHealth = player.getMaxHealth();
            double cal = maxHealth * 0.5;
            player.setHealth(cal);
            for (PotionEffect potionEffect : player.getActivePotionEffects()) {
                player.removePotionEffect(potionEffect.getType());
            }
            player.addPotionEffect(regenerationEffect);
            player.addPotionEffect(absorptionEffect);
        }
    }

}
