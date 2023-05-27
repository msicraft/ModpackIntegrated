package me.msicraft.modpackintegrated.CraftingEquip.Util;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class SpecialAbilityUtil {

    public static void applyLifeDrain(Player player, double amount) {
        double calHealth = player.getHealth() + amount;
        if (calHealth > player.getMaxHealth()) {
            calHealth = player.getMaxHealth();
        }
        player.setHealth(calHealth);
        player.sendMessage(ChatColor.DARK_GREEN + "생명력 흡수: " + ChatColor.GREEN + (Math.round(amount*100.0)/100.0));
    }

    public static boolean isDay(Player player) {
        boolean check = false;
        long time = player.getWorld().getTime();
        if (time > 1000 && time < 13000) {
            check = true;
        }
        return check;
    }

    public static boolean isNight(Player player) {
        return !isDay(player);
    }

    public static void healPlayer(Player player, double amount) {
        double currentHealth = player.getHealth();
        double cal = currentHealth + amount;
        if (cal > player.getMaxHealth()) {
            cal = player.getMaxHealth();
        }
        player.setHealth(cal);
    }

    public static void damageConvertHealth(Player player, double takeDamage, double percent) {
        double currentHealth = player.getHealth();
        double healingCal = takeDamage * percent;
        double cal = currentHealth + healingCal;
        if (cal > player.getMaxHealth()) {
            cal = player.getMaxHealth();
        }
        player.setHealth(cal);
    }

    public static void applyHalfHealth(Player player) {
        double currentHealth = player.getHealth();
        double cal = currentHealth * 0.5;
        double calR = Math.ceil(cal);
        player.setHealth(calR);
    }

    public static double getBaseMaxHealth(Entity entity, double percent) {
        double v = 0;
        if (entity instanceof LivingEntity livingEntity) {
            double maxHealth = livingEntity.getMaxHealth();
            double cal = maxHealth * percent;
            v = Math.round(cal*100.0)/100.0;
        }
        return v;
    }

    public static double getBaseCurrentHealth(Entity entity, double percent) {
        double v = 0;
        if (entity instanceof LivingEntity livingEntity) {
            double maxHealth = livingEntity.getHealth();
            double cal = maxHealth * percent;
            v = Math.round(cal*100.0)/100.0;
        }
        return v;
    }

    public static double getTrueDamage(double originalDamage, double percent) {
        return Math.round(originalDamage * percent * 100.0)/100.0;
    }

    public static void applyTrueDamage(Entity entity, double damage) {
        if (entity instanceof LivingEntity livingEntity) {
            double health = livingEntity.getHealth();
            double cal = health - damage;
            if (cal < 0) {
                cal = 0;
            }
            livingEntity.setHealth(cal);
        }
    }

}
