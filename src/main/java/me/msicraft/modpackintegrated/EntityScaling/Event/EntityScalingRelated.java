package me.msicraft.modpackintegrated.EntityScaling.Event;

import me.msicraft.modpackintegrated.EntityScaling.EntityScalingUtil;
import me.msicraft.modpackintegrated.ModPackIntegrated;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Arrays;
import java.util.List;

public class EntityScalingRelated implements Listener {

    private static boolean isEnabled = false;
    public static double maxHealthSpread = 1;
    public static double maxDamagePercent = 1;
    private static boolean isEnabledFixPlayerTakeDamageEvent = false;

    public static void reloadVariables() {
        isEnabled = ModPackIntegrated.getPlugin().getConfig().contains("EntityScaling.Enabled") && ModPackIntegrated.getPlugin().getConfig().getBoolean("EntityScaling.Enabled");
        maxHealthSpread = ModPackIntegrated.getPlugin().getConfig().contains("EntityScaling.MaxHealthSpread") ? ModPackIntegrated.getPlugin().getConfig().getDouble("EntityScaling.MaxHealthSpread") : 0;
        maxDamagePercent = ModPackIntegrated.getPlugin().getConfig().contains("EntityScaling.MaxDamagePercent") ? ModPackIntegrated.getPlugin().getConfig().getDouble("EntityScaling.MaxDamagePercent") : 0;
        isEnabledFixPlayerTakeDamageEvent = ModPackIntegrated.getPlugin().getConfig().contains("EntityScaling.Fix-PlayerTakeDamageEvent") && ModPackIntegrated.getPlugin().getConfig().getBoolean("EntityScaling.Fix-PlayerTakeDamageEvent");
    }

    private static final List<CreatureSpawnEvent.SpawnReason> blackListSpawnReason = Arrays.asList(CreatureSpawnEvent.SpawnReason.BEEHIVE);

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCreatureSpawn(CreatureSpawnEvent e) {
        if (isEnabled) {
            CreatureSpawnEvent.SpawnReason spawnReason = e.getSpawnReason();
            if (blackListSpawnReason.contains(spawnReason)) {
                return;
            }
            LivingEntity livingEntity = e.getEntity();
            if (livingEntity.getType() != EntityType.PLAYER) {
                AttributeInstance attackInstance = livingEntity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
                if (attackInstance != null) {
                    Bukkit.getScheduler().runTask(ModPackIntegrated.getPlugin(), () -> {
                        EntityScalingUtil.applyEntityScalingTag(livingEntity);
                        double randomPercentDamage = EntityScalingUtil.getRandomValueDouble(maxDamagePercent, 0);
                        EntityScalingUtil.applyRandomPercentDamageTag(livingEntity, randomPercentDamage);
                        AttributeInstance maxHealthInstance = livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                        if (maxHealthInstance != null) {
                            double currentValue = maxHealthInstance.getValue();
                            double randomSpreadValue = EntityScalingUtil.getRandomValueDouble(maxHealthSpread, 0);
                            double cal = currentValue + (currentValue * randomSpreadValue);
                            int roundCal = (int) Math.round(cal);
                            maxHealthInstance.setBaseValue(roundCal);
                            Bukkit.getScheduler().runTaskLater(ModPackIntegrated.getPlugin(), ()-> {
                                livingEntity.setHealth(livingEntity.getMaxHealth());
                            }, 1L);
                        }
                    });
                }
            }
        }
    }

    @EventHandler
    public void scalingEntityAttack(EntityDamageByEntityEvent e) {
        if (isEnabled && !isEnabledFixPlayerTakeDamageEvent) {
            Entity damager = e.getDamager();
            if (damager instanceof LivingEntity livingEntity) {
                if (livingEntity.getType() != EntityType.PLAYER) {
                    if (EntityScalingUtil.isScalingEntity(livingEntity)) {
                        double originalDamage = e.getDamage();
                        double randomPercentDamage = EntityScalingUtil.getPercentDamage(livingEntity);
                        double roundDamage = Math.round(randomPercentDamage * 100.0) / 100.0;
                        double cal = originalDamage + (originalDamage * roundDamage);
                        e.setDamage(cal);
                    }
                }
            }
        }
    }

    /*
    @EventHandler
    public void onFixPlayerTakeDamage(EntityDamageEvent e) {
        if (isEnabled && isEnabledFixPlayerTakeDamageEvent) {
            Entity entity = e.getEntity();
            if (entity.getType() == EntityType.PLAYER) {
                EntityDamageEvent.DamageCause damageCause = e.getCause();
            }
        }
    }

     */

}
