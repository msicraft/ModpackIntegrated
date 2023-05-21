package me.msicraft.modpackintegrated.Event;

import me.msicraft.modpackintegrated.Event.Task.EnderDragonExpTask;
import me.msicraft.modpackintegrated.ModPackIntegrated;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.projectiles.ProjectileSource;

import java.util.*;

public class EntityRelated implements Listener {

    public static void reloadVariables() {
        isEnabledReplaceNoDamageTicks = ModPackIntegrated.getPlugin().getConfig().contains("Setting.ReplaceNoDamageTicks.Enabled") && ModPackIntegrated.getPlugin().getConfig().getBoolean("Setting.ReplaceNoDamageTicks.Enabled");
        noDamageTicks = ModPackIntegrated.getPlugin().getConfig().contains("Setting.ReplaceNoDamageTicks.Ticks") ? ModPackIntegrated.getPlugin().getConfig().getInt("Setting.ReplaceNoDamageTicks.Ticks") : 10;
        isEnabledHealthRegen = ModPackIntegrated.getPlugin().getConfig().contains("Setting.FixHealthRegen.Enabled") && ModPackIntegrated.getPlugin().getConfig().getBoolean("Setting.FixHealthRegen.Enabled");
    }

    private void healingEntity(LivingEntity livingEntity, double amount) {
        double currentHealth = livingEntity.getHealth();
        double cal = currentHealth + amount;
        if (cal > livingEntity.getMaxHealth()) {
            cal = livingEntity.getMaxHealth();
        }
        livingEntity.setHealth(cal);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDisableDamage(EntityDamageEvent e) {
        if (e.getEntityType() != EntityType.PLAYER) {
            EntityDamageEvent.DamageCause damageCause = e.getCause();
            switch (damageCause) {
                case FALL,SUFFOCATION -> e.setDamage(0);
                case LAVA -> {
                    if (e.getEntityType() != EntityType.DROPPED_ITEM) {
                        e.setDamage(0);
                        Entity entity = e.getEntity();
                        if (entity instanceof LivingEntity livingEntity) {
                            healingEntity(livingEntity, 1);
                        }
                    }
                }
            }
        }
    }

    private static boolean isEnabledReplaceNoDamageTicks = false;
    private static int noDamageTicks = 10;
    private static List<EntityDamageEvent.DamageCause> allowDamageCauses = new ArrayList<>(Arrays.asList(EntityDamageEvent.DamageCause.ENTITY_ATTACK,
            EntityDamageEvent.DamageCause.ENTITY_EXPLOSION, EntityDamageEvent.DamageCause.PROJECTILE, EntityDamageEvent.DamageCause.THORNS,
            EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK, EntityDamageEvent.DamageCause.DRAGON_BREATH));

    @EventHandler(priority = EventPriority.LOWEST)
    public void replaceNoDamageTicks(EntityDamageEvent e) {
        if (isEnabledReplaceNoDamageTicks) {
            EntityDamageEvent.DamageCause damageCause = e.getCause();
            if (allowDamageCauses.contains(damageCause)) {
                if (e.getEntity() instanceof LivingEntity livingEntity) {
                    if (livingEntity.getType() == EntityType.PLAYER) {
                        Bukkit.getScheduler().runTask(ModPackIntegrated.getPlugin(), () -> {
                            livingEntity.setNoDamageTicks(1);
                        });
                    } else {
                        Bukkit.getScheduler().runTask(ModPackIntegrated.getPlugin(), () -> {
                            livingEntity.setNoDamageTicks(noDamageTicks);
                        });
                    }
                }
            }
        }
    }

    private static boolean isEnabledHealthRegen = false;
    private static final Map<UUID, Long> regenDelayMap = new HashMap<>();

    @EventHandler
    public void healthRegen(EntityRegainHealthEvent e) {
        if (isEnabledHealthRegen) {
            Entity entity = e.getEntity();
            if (entity instanceof LivingEntity livingEntity) {
                UUID uuid = livingEntity.getUniqueId();
                if (regenDelayMap.containsKey(uuid)) {
                    if (regenDelayMap.get(uuid) > System.currentTimeMillis()) {
                        e.setAmount(0);
                        return;
                    }
                }
                long coolDown = 1;
                regenDelayMap.put(uuid, (System.currentTimeMillis() + (coolDown * 1000)));
                double maxHealth = livingEntity.getMaxHealth();
                double cal;
                if (livingEntity.getType() == EntityType.PLAYER) {
                    //double playerHealthRegenPercent = 0.01;
                    cal = maxHealth * 0.05;
                } else {
                    //double nonPlayerHealthRegenPercent = 0.05;
                    cal = maxHealth * 0.1;
                }
                e.setAmount(cal);
            }
        }
    }

    @EventHandler
    public void removeHealthRegenMapRemove(EntityDeathEvent e) {
        if (isEnabledHealthRegen) {
            if (e.getEntityType() != EntityType.PLAYER) {
                UUID uuid = e.getEntity().getUniqueId();
                regenDelayMap.remove(uuid);
            }
        }
    }

    @EventHandler
    public void onEntityDespawn(ChunkUnloadEvent e) {
        if (isEnabledHealthRegen) {
            Entity[] entities = e.getChunk().getEntities();
            for (Entity entity : entities) {
                if (entity instanceof LivingEntity livingEntity) {
                    if (livingEntity.getType() != EntityType.PLAYER) {
                        regenDelayMap.remove(livingEntity.getUniqueId());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityArrowDamage(EntityDamageByEntityEvent e) {
        Entity damager = e.getDamager();
        if (damager instanceof Arrow arrow) {
            ProjectileSource projectileSource = arrow.getShooter();
            if (projectileSource instanceof LivingEntity livingEntity) {
                AttributeInstance instance = livingEntity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
                double originalDamage = e.getDamage();
                double cal;
                if (instance != null) {
                    double attributeDamage = instance.getValue();
                    cal = originalDamage + attributeDamage;
                } else {
                    cal = originalDamage;
                }
                e.setDamage(cal);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void fixDropExp(EntityDeathEvent e) {
        if (e.getEntityType() == EntityType.ENDER_DRAGON) {
            new EnderDragonExpTask(e.getEntity()).runTaskTimer(ModPackIntegrated.getPlugin(), 70L, 3L);
        }
    }

}
