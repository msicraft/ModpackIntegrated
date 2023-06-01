package me.msicraft.modpackintegrated.Event;

import me.msicraft.modpackintegrated.CraftingEquip.Doppelganger.DoppelgangerUtil;
import me.msicraft.modpackintegrated.ModPackIntegrated;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ExpBottleEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.raid.RaidSpawnWaveEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;

import java.util.Random;

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

    private final Random random = new Random();

    @EventHandler
    public void onRaidDoppelgangerSpawn(RaidSpawnWaveEvent e) {
        if (Math.random() < 0.5) {
            Raid raid = e.getRaid();
            Player player = null;
            int maxPlayers = Bukkit.getOnlinePlayers().size();
            int spawnCount = 1;
            for (int a = 0; a<maxPlayers; a++) {
                if (Math.random() < 0.1) {
                    spawnCount++;
                }
            }
            for (int b = 0; b<spawnCount; b++) {
                int randomP = random.nextInt(maxPlayers);
                int count = 0;
                for (Player onlineP : Bukkit.getOnlinePlayers()) {
                    if (count == randomP) {
                        player = onlineP;
                        break;
                    } else {
                        count++;
                    }
                }
                if (player != null) {
                    for (Raider raider : raid.getRaiders()) {
                        if (raider.getType() == EntityType.VINDICATOR) {
                            if (!DoppelgangerUtil.isDoppelganger(raider)) {
                                Player finalPlayer = player;
                                Bukkit.getScheduler().runTaskLater(ModPackIntegrated.getPlugin(), ()->{
                                    DoppelgangerUtil.replaceEquipment(raider, finalPlayer);
                                    if (ModPackIntegrated.isDebugEnabled) {
                                        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "========================================");
                                        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "레이드에 도플갱어 참여");
                                        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "대상 플레이어: " + ChatColor.GRAY + finalPlayer);
                                        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "========================================");
                                    }
                                }, 10L);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

}
