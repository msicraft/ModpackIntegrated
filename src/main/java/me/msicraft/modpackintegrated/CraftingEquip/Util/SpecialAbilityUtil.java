package me.msicraft.modpackintegrated.CraftingEquip.Util;

import org.bukkit.ChatColor;
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

}
