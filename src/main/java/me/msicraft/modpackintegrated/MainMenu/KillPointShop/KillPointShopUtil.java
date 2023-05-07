package me.msicraft.modpackintegrated.MainMenu.KillPointShop;

import me.msicraft.modpackintegrated.ModPackIntegrated;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class KillPointShopUtil {

    public List<String> getShopItemInternalNames() {
        List<String> list = new ArrayList<>();
        ConfigurationSection section = ModPackIntegrated.getPlugin().getConfig().getConfigurationSection("KillPoint-Shop");
        if (section != null) {
            list.addAll(section.getKeys(false));
        }
        return list;
    }

    public boolean isEnabled(String name) {
        boolean check = false;
        if (ModPackIntegrated.getPlugin().getConfig().contains("KillPoint-Shop." + name + ".Enabled")) {
            check = ModPackIntegrated.getPlugin().getConfig().getBoolean("KillPoint-Shop." + name + ".Enabled");
        }
        return check;
    }

    public int getRequiredKillPoint(String name) {
        int value = -1;
        if (ModPackIntegrated.getPlugin().getConfig().contains("KillPoint-Shop." + name + ".Required-KillPoint")) {
            value = ModPackIntegrated.getPlugin().getConfig().getInt("KillPoint-Shop." + name + ".Required-KillPoint");
        }
        return value;
    }

    public Material getMaterial(String name) {
        Material material = Material.STONE;
        if (ModPackIntegrated.getPlugin().getConfig().contains("KillPoint-Shop." + name + ".Material")) {
            String getMaterialName = ModPackIntegrated.getPlugin().getConfig().getString("KillPoint-Shop." + name + ".Material");
            if (getMaterialName != null) {
                try {
                    material = Material.valueOf(getMaterialName.toUpperCase());
                } catch (IllegalArgumentException | NullPointerException ignored) {
                }
            }
        }
        return material;
    }

    public String getName(String name) {
        String a = null;
        if (ModPackIntegrated.getPlugin().getConfig().contains("KillPoint-Shop." + name + ".Name")) {
            a = ModPackIntegrated.getPlugin().getConfig().getString("KillPoint-Shop." + name + ".Name");
            if (a != null) {
                a = ChatColor.translateAlternateColorCodes('&', a);
            }
        }
        return a;
    }

    public List<String> getLoreList(String name) {
        List<String> list = new ArrayList<>();
        if (ModPackIntegrated.getPlugin().getConfig().contains("KillPoint-Shop." + name + ".Lore")) {
            List<String> temp = ModPackIntegrated.getPlugin().getConfig().getStringList("KillPoint-Shop." + name + ".Lore");
            for (String s : temp) {
                s = s.replaceAll("<requiredKillPoint>", String.valueOf(getRequiredKillPoint(name)));
                list.add(ChatColor.translateAlternateColorCodes('&', s));
            }
        }
        return list;
    }

}
