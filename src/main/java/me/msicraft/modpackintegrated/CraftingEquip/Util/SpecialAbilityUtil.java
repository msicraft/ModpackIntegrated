package me.msicraft.modpackintegrated.CraftingEquip.Util;

import org.bukkit.entity.Player;

public class SpecialAbilityUtil {

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
