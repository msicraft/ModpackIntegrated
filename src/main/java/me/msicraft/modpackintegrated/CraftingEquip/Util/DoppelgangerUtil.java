package me.msicraft.modpackintegrated.CraftingEquip.Util;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class DoppelgangerUtil {

    public static double getPlayerMaxHealth(Player player) {
        return player.getMaxHealth();
    }

    public static double getPlayerExtraHealth(Player player) {
        double d = 0;
        if (CraftingEquipStatUtil.containStatMap(player)) {
            d = CraftingEquipStatUtil.getStatMap(player).get("Health");
        }
        return d;
    }

    public static void setMaxHealth(LivingEntity livingEntity, double amount) {
        AttributeInstance instance = livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (instance != null) {
            double cal = Math.round(amount*100.0)/100.0;
            instance.setBaseValue(cal);
            livingEntity.setHealth(cal);
        }
    }

    public static double getPlayerDamage(Player player) {
        double d = 4;
        AttributeInstance instance = player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
        if (instance != null) {
            d = instance.getValue();
        }
        return d;
    }

    public static double getPlayerExtraDamage(Player player) {
        double d = 0;
        if (CraftingEquipStatUtil.containStatMap(player)) {
            d = CraftingEquipStatUtil.getStatMap(player).get("MeleeDamage");
        }
        return d;
    }

    public static void setBaseDamage(LivingEntity livingEntity, double amount) {
        AttributeInstance instance = livingEntity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
        if (instance != null) {
            instance.setBaseValue(amount);
        }
    }

}
