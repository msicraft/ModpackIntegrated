package me.msicraft.modpackintegrated.CraftingEquip.Util;

import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class SpecialAbilityUtil {

    public static double calAbilityCoolDown(Player player, double baseCoolDown) {
        double cd = 1;
        AttributeInstance instance = player.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
        if (instance != null) {
            double attackSpeed = instance.getValue();
            cd = baseCoolDown/attackSpeed;
        }
        return Math.round(cd * 100.0)/100.0;
    }

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

    public static double getPlayerMaxHealthPercent(Player player, double percent) {
        double maxHealth = player.getMaxHealth();
        return Math.round(maxHealth * percent * 100.0)/100.0;
    }

    public static boolean isBackAttack(Player player, Entity entity) {
        boolean check = false;
        Vector pDir = player.getLocation().getDirection();
        Vector eDir = entity.getLocation().getDirection();
        double relativeAngle = (Math.atan2(pDir.getX() * eDir.getZ() - pDir.getZ() * eDir.getX(), pDir.getX() * eDir.getX() + pDir.getZ() * eDir.getZ()) * 180) / Math.PI;
        if (relativeAngle <= 60 && relativeAngle >= -60) {
            check = true;
        }
        return check;
    }

}
