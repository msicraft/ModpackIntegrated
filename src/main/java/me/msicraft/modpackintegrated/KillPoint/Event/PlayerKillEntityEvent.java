package me.msicraft.modpackintegrated.KillPoint.Event;

import me.msicraft.modpackintegrated.KillPoint.KillPointUtil;
import me.msicraft.modpackintegrated.ModPackIntegrated;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class PlayerKillEntityEvent implements Listener {

    private static boolean isEnableFixEvent = false;
    private static boolean isEnabled = false;
    private static boolean isEnabledDistributionKillPointExp = false;

    public static void reloadVariables() {
        isEnabled = ModPackIntegrated.getPlugin().getConfig().contains("Kill-Point-Setting.Enabled") && ModPackIntegrated.getPlugin().getConfig().getBoolean("Kill-Point-Setting.Enabled");
        isEnableFixEvent = ModPackIntegrated.getPlugin().getConfig().contains("Kill-Point-Setting.Fix-DeathEvent") && ModPackIntegrated.getPlugin().getConfig().getBoolean("Kill-Point-Setting.Fix-DeathEvent");
        isEnabledDistributionKillPointExp = ModPackIntegrated.getPlugin().getConfig().contains("Kill-Point-Setting.DistributionKillPointExp") && ModPackIntegrated.getPlugin().getConfig().getBoolean("Kill-Point-Setting.DistributionKillPointExp");
    }

    private double getDamageValue(LivingEntity livingEntity) {
        double value = 0;
        AttributeInstance instance = livingEntity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
        if (instance != null) {
            value = instance.getValue();
        }
        return value;
    }

    private double getArmorValue(LivingEntity livingEntity) {
        double value = 0;
        AttributeInstance instance = livingEntity.getAttribute(Attribute.GENERIC_ARMOR);
        if (instance != null) {
            value = instance.getValue();
        }
        return value;
    }

    private double getArmorToughnessValue(LivingEntity livingEntity) {
        double value = 0;
        AttributeInstance instance = livingEntity.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS);
        if (instance != null) {
            value = instance.getValue();
        }
        return value;
    }

    private ItemStack getKillPointItemStack(double value) {
        ItemStack itemStack = new ItemStack(Material.BOOK, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        PersistentDataContainer data = itemMeta.getPersistentDataContainer();
        itemMeta.setDisplayName(ChatColor.WHITE + "Kill Point Exp");
        itemMeta.addEnchant(Enchantment.DURABILITY, 1, false);
        data.set(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI_KillPointItem"), PersistentDataType.STRING, "MPI_KillPointItem");
        data.set(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI_KillPointItem-Value"), PersistentDataType.STRING, String.valueOf(value));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    @EventHandler
    public void onSpawnerMobTag(CreatureSpawnEvent e) {
        if (e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER || e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER_EGG) {
            PersistentDataContainer data = e.getEntity().getPersistentDataContainer();
            data.set(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI-SpawnerEntity"), PersistentDataType.STRING, "MPI-Spawner");
        }
    }

    private boolean hasSpawnerTag(LivingEntity livingEntity) {
        boolean check = false;
        PersistentDataContainer data = livingEntity.getPersistentDataContainer();
        if (data.has(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI-SpawnerEntity"), PersistentDataType.STRING)) {
            check = true;
        }
        return check;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerKillEntity(EntityDeathEvent e) {
        if (isEnabled && !isEnableFixEvent) {
            String killPointExpEquations = KillPointUtil.getKillPointExpEquations();
            if (killPointExpEquations != null) {
                LivingEntity livingEntity = e.getEntity();
                Player player = livingEntity.getKiller();
                double exp;
                double maxHealth = livingEntity.getMaxHealth();
                double damage = getDamageValue(livingEntity);
                double armor = getArmorValue(livingEntity);
                double armorToughness = getArmorToughnessValue(livingEntity);
                if (player != null) {
                    exp = KillPointUtil.getToEntityKillPointExp(maxHealth, damage, armor, armorToughness);
                    boolean isSpawner = hasSpawnerTag(livingEntity);
                    if (isSpawner) {
                        exp = exp * 0.25;
                    }
                    if (isEnabledDistributionKillPointExp) {
                        List<Player> nearPlayers = new ArrayList<>();
                        for (Entity entity : livingEntity.getNearbyEntities(15, 5, 15)) {
                            if (entity instanceof Player p) {
                                nearPlayers.add(p);
                            }
                        }
                        if (!nearPlayers.isEmpty()) {
                            double perPlayerExp = exp / nearPlayers.size();
                            for (Player np : nearPlayers) {
                                KillPointUtil.addKillPointExp(np, perPlayerExp);
                            }
                            if (ModPackIntegrated.isDebugEnabled) {
                                Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "===================================");
                                Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "플레이어 킬 포인트 경험치 분배");
                                Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Living Entity: " + ChatColor.GREEN + livingEntity.getCustomName() + " | " + livingEntity.getType() + " | " + livingEntity.getName());
                                Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Killer Player: " + ChatColor.GREEN + player.getName());
                                Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "총 플레이어: " + ChatColor.GREEN + nearPlayers.size());
                                Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "플레이어: " + ChatColor.GREEN + nearPlayers);
                                Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "총 경험치: " + ChatColor.GREEN + exp);
                                Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "분배 경험치: " + ChatColor.GREEN + perPlayerExp);
                                Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "스포너 몹: " + ChatColor.GREEN + isSpawner);
                                Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "===================================");
                            }
                        }
                    } else {
                        KillPointUtil.addKillPointExp(player, exp);
                        if (ModPackIntegrated.isDebugEnabled) {
                            Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "===================================");
                            Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "PlayerKillEntity Event");
                            Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Living Entity: " + ChatColor.GREEN + livingEntity.getCustomName() + " | " + livingEntity.getType() + " | " + livingEntity.getName());
                            Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Killer Player: " + ChatColor.GREEN + player.getName());
                            Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Health: " + ChatColor.GREEN + livingEntity.getMaxHealth() + " | Damage: " + getDamageValue(livingEntity));
                            Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Armor: " + ChatColor.GREEN + getArmorValue(livingEntity) + " | ArmorToughness: " + getArmorToughnessValue(livingEntity));
                            Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Equations: " + ChatColor.GREEN + killPointExpEquations);
                            Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Get KillPoint Exp: " + ChatColor.GREEN + exp);
                            Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "===================================");
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerKillEntityFix(EntityDamageByEntityEvent e) {
        if (isEnabled && isEnableFixEvent) {
            String killPointExpEquations = KillPointUtil.getKillPointExpEquations();
            if (killPointExpEquations != null) {
                if (e.getEntityType() != EntityType.PLAYER) {
                    Entity entity = e.getEntity();
                    if (entity instanceof LivingEntity livingEntity) {
                        double currentHealth = livingEntity.getHealth();
                        double finalDamage = e.getFinalDamage();
                        if (currentHealth - finalDamage <= 0) {
                            Bukkit.getScheduler().runTask(ModPackIntegrated.getPlugin(), () -> {
                                if (livingEntity.isDead() || livingEntity.getHealth() <= 0) {
                                    double exp;
                                    double maxHealth = livingEntity.getMaxHealth();
                                    double damage = getDamageValue(livingEntity);
                                    double armor = getArmorValue(livingEntity);
                                    double armorToughness = getArmorToughnessValue(livingEntity);
                                    if (damage <= 0) {
                                        exp = 0;
                                    } else {
                                        exp = KillPointUtil.getToEntityKillPointExp(maxHealth, damage, armor, armorToughness);
                                    }
                                    ItemStack itemStack = getKillPointItemStack(exp);
                                    Location location = livingEntity.getLocation();
                                    World world = livingEntity.getWorld();
                                    world.dropItemNaturally(location, itemStack);
                                    Bukkit.getScheduler().runTask(ModPackIntegrated.getPlugin(), () -> {
                                        for (Entity nearbyItem : world.getNearbyEntities(location, 2.5, 1, 2.5)) {
                                            if (nearbyItem instanceof Item item) {
                                                ItemMeta itemMeta = item.getItemStack().getItemMeta();
                                                if (itemMeta != null) {
                                                    PersistentDataContainer data = itemMeta.getPersistentDataContainer();
                                                    if (data.has(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI_KillPointItem"), PersistentDataType.STRING)) {
                                                        item.setCustomName(ChatColor.BOLD + "" + ChatColor.GREEN + "Kill Point");
                                                        item.setCustomNameVisible(true);
                                                    }
                                                }
                                            }
                                        }
                                    });
                                    if (ModPackIntegrated.isDebugEnabled) {
                                        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "===================================");
                                        Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "PlayerKillEntity Fix Event");
                                        Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Living Entity: " + ChatColor.GREEN + livingEntity.getCustomName() + " | " + livingEntity.getType());
                                        Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Equations: " + ChatColor.GREEN + killPointExpEquations);
                                        Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Drop KillPoint Exp: " + ChatColor.GREEN + exp);
                                        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "===================================");
                                    }
                                }
                            });
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void pickUpKillPointItem(EntityPickupItemEvent e) {
        LivingEntity livingEntity = e.getEntity();
        if (livingEntity instanceof Player player) {
            Item item = e.getItem();
            ItemStack itemStack = item.getItemStack();
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta != null) {
                PersistentDataContainer data = itemMeta.getPersistentDataContainer();
                if (data.has(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI_KillPointItem"), PersistentDataType.STRING)) {
                    e.setCancelled(true);
                    String s = data.get(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI_KillPointItem-Value"), PersistentDataType.STRING);
                    if (s != null) {
                        double value = Double.parseDouble(s);
                        KillPointUtil.addKillPointExp(player, value);
                        if (ModPackIntegrated.isDebugEnabled) {
                            Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "===================================");
                            Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Player Pickup KillPoint item");
                            Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Player: " + ChatColor.GREEN + player.getName());
                            Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Drop KillPoint Exp: " + ChatColor.GREEN + value);
                            Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "===================================");
                        }
                    }
                    item.remove();
                }
            }
        }
    }

}
