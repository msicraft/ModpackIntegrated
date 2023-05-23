package me.msicraft.modpackintegrated.CraftingEquip.Event;

import me.msicraft.modpackintegrated.CraftingEquip.Enum.SpecialAbility;
import me.msicraft.modpackintegrated.CraftingEquip.Util.CraftingEquipSpecialAbility;
import me.msicraft.modpackintegrated.CraftingEquip.Util.CraftingEquipStatUtil;
import me.msicraft.modpackintegrated.CraftingEquip.Util.CraftingEquipUtil;
import me.msicraft.modpackintegrated.CraftingEquip.Util.DoppelgangerUtil;
import me.msicraft.modpackintegrated.ModPackIntegrated;
import me.msicraft.modpackintegrated.Util.MathUtil;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CraftingEquipEvent implements Listener {

    private final Random random = new Random();

    private static String defenseEquations = null;
    private static Expression expression = null;

    public static String getDefenseEquations() {
        return defenseEquations;
    }

    public static void reloadVariables() {
        defenseEquations = ModPackIntegrated.getPlugin().getConfig().contains("CraftingEquipment.DefenseEquations") ? ModPackIntegrated.getPlugin().getConfig().getString("CraftingEquipment.DefenseEquations") : null;
        if (defenseEquations == null) {
            defenseEquations = "DA * (1 - (DE / (5 * DA + DE)))";
        }
        try {
            expression = new ExpressionBuilder(defenseEquations).variables("DA","DE").build();
        } catch (IllegalArgumentException e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "잘못된 표현식 발견: " + ChatColor.GREEN + defenseEquations);
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "기본 표현식이 사용됩니다. " + " DA * (1 - (DE / (5 * DA + DE)))");
            defenseEquations = "DA * (1 - (DE / (5 * DA + DE)))";
        }
    }

    private static final List<SpecialAbility> lowPriorityAbilities = Arrays.asList(SpecialAbility.doubleDamage_5, SpecialAbility.doubleDamage_10,
            SpecialAbility.doubleDamage_15, SpecialAbility.lifeDrain_5_25, SpecialAbility.lifeDrain_5_50, SpecialAbility.lifeDrain_10_25, SpecialAbility.lifeDrain_10_50);

    @EventHandler
    public void onPlayerMeleeAttack(EntityDamageByEntityEvent e) {
        Entity damager = e.getDamager();
        Entity entity = e.getEntity();
        if (damager instanceof Player player) {
            if (e.getCause() != EntityDamageEvent.DamageCause.PROJECTILE) {
                double originalDamage = e.getDamage();
                double cal = originalDamage + CraftingEquipStatUtil.getMeleeValue(player);
                if (CraftingEquipStatUtil.hasSpecialAbilityEquipment(player)) {
                    List<SpecialAbility> list = CraftingEquipStatUtil.getContainSpecialAbilities(player);
                    if (!list.isEmpty()) {
                        List<SpecialAbility> lowPriorities = new ArrayList<>();
                        for (SpecialAbility specialAbility : list) {
                            if (lowPriorityAbilities.contains(specialAbility)) {
                                lowPriorities.add(specialAbility);
                            } else {
                                cal = CraftingEquipSpecialAbility.applySpecialAbilityByPlayerAttack(player, cal, specialAbility, entity);
                            }
                        }
                        if (!lowPriorities.isEmpty()) {
                            for (SpecialAbility specialAbility : lowPriorities) {
                                cal = CraftingEquipSpecialAbility.applySpecialAbilityByPlayerAttack(player, cal, specialAbility, entity);
                            }
                        }
                        cal = Math.floor(cal*100)/100.0;
                    }
                }
                e.setDamage(cal);
            }
        }
    }

    @EventHandler
    public void onPlayerArrowAttack(EntityDamageByEntityEvent e) {
        if (e.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
            Entity damager = e.getDamager();
            Entity entity = e.getEntity();
            if (damager instanceof Arrow arrow) {
                if (arrow.getShooter() instanceof Player player) {
                    double originalDamage = e.getDamage();
                    double cal = originalDamage + CraftingEquipStatUtil.getProjectileValue(player);
                    if (CraftingEquipStatUtil.hasSpecialAbilityEquipment(player)) {
                        List<SpecialAbility> list = CraftingEquipStatUtil.getContainSpecialAbilities(player);
                        if (!list.isEmpty()) {
                            List<SpecialAbility> lowPriorities = new ArrayList<>();
                            for (SpecialAbility specialAbility : list) {
                                if (lowPriorityAbilities.contains(specialAbility)) {
                                    lowPriorities.add(specialAbility);
                                } else {
                                    cal = CraftingEquipSpecialAbility.applySpecialAbilityByPlayerAttack(player, cal, specialAbility, entity);
                                }
                            }
                            if (!lowPriorities.isEmpty()) {
                                for (SpecialAbility specialAbility : lowPriorities) {
                                    cal = CraftingEquipSpecialAbility.applySpecialAbilityByPlayerAttack(player, cal, specialAbility, entity);
                                }
                            }
                            cal = Math.floor(cal*100)/100.0;
                        }
                    }
                    e.setDamage(cal);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerTakeDamage(EntityDamageByEntityEvent e) {
        Entity entity = e.getEntity();
        if (entity instanceof Player player) {
            double originalDamage = e.getDamage();
            if (defenseEquations != null && expression != null) {
                if (player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
                    PotionEffect potionEffect = player.getPotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                    if (potionEffect != null) {
                        int level = potionEffect.getAmplifier() + 1;
                        originalDamage = originalDamage + (originalDamage * (level * 0.1));
                    }
                }
                double getDefense = CraftingEquipStatUtil.getDefenseValue(player);
                double v = expression.setVariable("DA", originalDamage).setVariable("DE", getDefense).evaluate();
                v = Math.round(v * 100.0) / 100.0;
                if (CraftingEquipStatUtil.hasSpecialAbilityEquipment(player)) {
                    List<SpecialAbility> list = CraftingEquipStatUtil.getContainSpecialAbilities(player);
                    if (!list.isEmpty()) {
                        for (SpecialAbility specialAbility : list) {
                            v = CraftingEquipSpecialAbility.applySpecialAbilityByTakeDamage(player, v, specialAbility);
                        }
                    }
                }
                e.setDamage(v);
            }
        }
    }

    @EventHandler
    public void onDropCraftingEquipment(EntityDeathEvent e) {
        if (Math.random() < 0.05) {
            LivingEntity livingEntity = e.getEntity();
            ItemStack itemStack = CraftingEquipUtil.createRandomEquipment();
            Location location = livingEntity.getLocation();
            World world = livingEntity.getWorld();
            world.dropItemNaturally(location, itemStack);
            Bukkit.getScheduler().runTask(ModPackIntegrated.getPlugin(), ()-> {
                for (Entity entity : world.getNearbyEntities(location, 4,3,4)) {
                    if (entity instanceof Item item) {
                        ItemStack itemStack1 = item.getItemStack();
                        if (CraftingEquipUtil.isRandomCraftingEquipment(itemStack1)) {
                            item.setCustomName(ChatColor.AQUA + "제작된 장비");
                            item.setGlowing(true);
                            item.setCustomNameVisible(true);
                        }
                    }
                }
            });
        }
    }

    @EventHandler
    public void spawnDoppelganger(EntityDeathEvent e) {
        if (Math.random() < 0.01) {
            Player player = e.getEntity().getKiller();
            if (player != null) {
                Location location = e.getEntity().getLocation();
                World world = location.getWorld();
                if (world != null) {
                    ItemStack helmet = player.getInventory().getHelmet();
                    ItemStack chest = player.getInventory().getChestplate();
                    ItemStack leggings = player.getInventory().getLeggings();
                    ItemStack boots = player.getInventory().getBoots();
                    ItemStack hand = player.getInventory().getItemInMainHand();
                    ItemStack offHand = player.getInventory().getItemInOffHand();
                    Bukkit.getScheduler().runTask(ModPackIntegrated.getPlugin(), () -> world.spawn(location, Husk.class, husk -> {
                        husk.setCustomName(player.getName() + " 의 장비를 복제한 좀비");
                        husk.setCustomNameVisible(false);
                        husk.setCanPickupItems(false);
                        EntityEquipment entityEquipment = husk.getEquipment();
                        if (entityEquipment != null) {
                            if (helmet != null && helmet.getType() != Material.AIR) {
                                entityEquipment.setHelmet(new ItemStack(helmet));
                            }
                            if (chest != null && chest.getType() != Material.AIR) {
                                entityEquipment.setChestplate(new ItemStack(chest));
                            }
                            if (leggings != null && leggings.getType() != Material.AIR) {
                                entityEquipment.setLeggings(new ItemStack(leggings));
                            }
                            if (boots != null && boots.getType() != Material.AIR) {
                                entityEquipment.setBoots(new ItemStack(boots));
                            }
                            if (hand != null && hand.getType() != Material.AIR) {
                                entityEquipment.setItemInMainHand(new ItemStack(hand));
                            }
                            if (offHand != null && offHand.getType() != Material.AIR) {
                                entityEquipment.setItemInOffHand(new ItemStack(offHand));
                            }
                        }
                        double baseMaxHealth = DoppelgangerUtil.getPlayerMaxHealth(player);
                        double baseDamage = DoppelgangerUtil.getPlayerExtraDamage(player);
                        double randomHealthMultiple = MathUtil.getRandomValueDouble(10.1, 1.1);
                        double randomDamageMultiple = MathUtil.getRandomValueDouble(5.1, 1.1);
                        double calHealth = baseMaxHealth + (baseMaxHealth * randomHealthMultiple);
                        double calDamage = baseDamage + (baseDamage * randomDamageMultiple);
                        DoppelgangerUtil.setMaxHealth(husk, calHealth);
                        DoppelgangerUtil.setBaseDamage(husk, calDamage);
                        PersistentDataContainer data = husk.getPersistentDataContainer();
                        data.set(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI-Doppelganger"), PersistentDataType.STRING, husk.getUniqueId().toString());
                    }));
                }
            }
        }
    }

    @EventHandler
    public void disableDoppelgangerDrop(EntityDeathEvent e) {
        LivingEntity livingEntity = e.getEntity();
        PersistentDataContainer data = livingEntity.getPersistentDataContainer();
        if (data.has(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI-Doppelganger"), PersistentDataType.STRING)) {
            e.getDrops().clear();
        }
    }

}
