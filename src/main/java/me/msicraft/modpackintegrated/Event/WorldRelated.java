package me.msicraft.modpackintegrated.Event;

import me.msicraft.modpackintegrated.ModPackIntegrated;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ExpBottleEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;

public class WorldRelated implements Listener {

    private static boolean disableMount = true;
    private static boolean disableBoneMeal = true;

    public static void reloadVariables() {
        disableMount = !ModPackIntegrated.getPlugin().getConfig().contains("Setting.Disable-Mount") || ModPackIntegrated.getPlugin().getConfig().getBoolean("Setting.Disable-Mount");
        disableBoneMeal = !ModPackIntegrated.getPlugin().getConfig().contains("Setting.Disable-BoneMeal") || ModPackIntegrated.getPlugin().getConfig().getBoolean("Setting.Disable-BoneMeal");
    }

    @EventHandler
    public void EntityVehicle(VehicleEnterEvent e) {
        if (disableMount) {
            if (e.getEntered().getType() != EntityType.PLAYER) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDisableBoneMeal(PlayerInteractEvent e) {
        if (disableBoneMeal) {
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getItem() != null && e.getItem().getType() == Material.BONE_MEAL) {
                e.setCancelled(true);
            }
        }
    }

    private final int expCap = 5;
    private final int mergeCap = 100;

    @EventHandler
    public void onExpBottle(ExpBottleEvent e) {
        Location location = e.getEntity().getLocation();
        if (e.getExperience() > expCap) {
            e.setExperience(expCap);
        }
        World world = location.getWorld();
        Bukkit.getScheduler().runTaskLater(ModPackIntegrated.getPlugin(), ()-> {
            if (world != null) {
                for (Entity entity : world.getNearbyEntities(location, 5, 4, 5)) {
                    if (entity instanceof ExperienceOrb experienceOrb) {
                        int orbExp = experienceOrb.getExperience();
                        if (orbExp > mergeCap) {
                            experienceOrb.setExperience(mergeCap);
                        }
                    }
                }
            }
        }, 1L);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityExp(EntityDeathEvent e) {
        if (e.getEntityType() != EntityType.PLAYER) {
            LivingEntity livingEntity = e.getEntity();
            World world = livingEntity.getWorld();
            Bukkit.getScheduler().runTask(ModPackIntegrated.getPlugin(), ()-> {
                Location location = livingEntity.getLocation();
                for (Entity entity : world.getNearbyEntities(location, 4, 3, 4)) {
                    if (entity instanceof ExperienceOrb experienceOrb) {
                        int orbExp = experienceOrb.getExperience();
                        if (orbExp > mergeCap) {
                            experienceOrb.setExperience(mergeCap);
                        }
                    }
                }
            });
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDisableBedExplode(PlayerBedEnterEvent e) {
        Player player = e.getPlayer();
        if (player.getWorld().getEnvironment() != World.Environment.NORMAL) {
            e.setCancelled(true);
        }
    }


}
