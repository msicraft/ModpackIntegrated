package me.msicraft.modpackintegrated.CraftingEquip;

import me.msicraft.modpackintegrated.ModPackIntegrated;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;

public class DamageIndicator {

    public static void spawnCriticalIndicator(Location location, double value) {
        World world = location.getWorld();
        if (world != null) {
            double damageCal = (Math.round(value * 100) / 100.0);
            String indicator = ChatColor.BOLD + "" + ChatColor.RED + "Crit " + damageCal;
            Location spawnLoc = new Location(world, location.getX(), location.getY() + 2, location.getZ(), location.getYaw(), location.getPitch());
            ArmorStand armorStand = location.getWorld().spawn(spawnLoc, ArmorStand.class, stand -> {
                stand.setBasePlate(false);
                stand.setCustomName(indicator);
                stand.setCustomNameVisible(true);
                stand.setInvulnerable(true);
                stand.setVisible(false);
                stand.setGravity(false);
                stand.setMarker(true);
            });
            Bukkit.getScheduler().runTaskLater(ModPackIntegrated.getPlugin(), armorStand::remove, 20L);
        }
    }

    public static void spawnLifeStealIndicator(Location location, double value) {
        World world = location.getWorld();
        if (world != null) {
            double damageCal = (Math.round(value * 100) / 100.0);
            String indicator = ChatColor.BOLD + "" + ChatColor.DARK_GREEN + damageCal;
            Location spawnLoc = new Location(world, location.getX(), location.getY() + 2, location.getZ(), location.getYaw(), location.getPitch());
            ArmorStand armorStand = location.getWorld().spawn(spawnLoc, ArmorStand.class, stand -> {
                stand.setBasePlate(false);
                stand.setCustomName(indicator);
                stand.setCustomNameVisible(true);
                stand.setInvulnerable(true);
                stand.setVisible(false);
                stand.setGravity(false);
                stand.setMarker(true);
            });
            Bukkit.getScheduler().runTaskLater(ModPackIntegrated.getPlugin(), armorStand::remove, 20L);
        }
    }

}
