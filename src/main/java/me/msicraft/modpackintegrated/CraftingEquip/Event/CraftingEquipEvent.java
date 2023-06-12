package me.msicraft.modpackintegrated.CraftingEquip.Event;

import me.msicraft.modpackintegrated.CraftingEquip.Doppelganger.DoppelgangerUtil;
import me.msicraft.modpackintegrated.CraftingEquip.Enum.SpecialAbility;
import me.msicraft.modpackintegrated.CraftingEquip.Util.CraftingEquipSpecialAbility;
import me.msicraft.modpackintegrated.CraftingEquip.Util.CraftingEquipStatUtil;
import me.msicraft.modpackintegrated.CraftingEquip.Util.CraftingEquipUtil;
import me.msicraft.modpackintegrated.CraftingEquip.Util.SpecialAbilityInfo;
import me.msicraft.modpackintegrated.ModPackIntegrated;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
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

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerMeleeAttack(EntityDamageByEntityEvent e) {
        Entity damager = e.getDamager();
        Entity entity = e.getEntity();
        if (damager instanceof Player player) {
            if (e.getCause() != EntityDamageEvent.DamageCause.PROJECTILE) {
                if (e.getFinalDamage() < 0 || Double.compare(e.getFinalDamage(), 0) == 0) {
                    return;
                }
                double originalDamage = e.getDamage();
                double cal = originalDamage + CraftingEquipStatUtil.getMeleeValue(player);
                if (CraftingEquipStatUtil.hasSpecialAbilityEquipment(player)) {
                    List<SpecialAbility> list = CraftingEquipStatUtil.getContainSpecialAbilities(player);
                    if (!list.isEmpty()) {
                        List<SpecialAbility> highPriorities = new ArrayList<>();
                        for (SpecialAbility specialAbility : list) {
                            if (SpecialAbilityInfo.highPriorityAbilities.contains(specialAbility)) {
                                highPriorities.add(specialAbility);
                            } else {
                                cal = CraftingEquipSpecialAbility.applySpecialAbilityByPlayerAttack(player, cal, specialAbility, entity);
                            }
                        }
                        if (!highPriorities.isEmpty()) {
                            for (SpecialAbility specialAbility : highPriorities) {
                                cal = CraftingEquipSpecialAbility.applySpecialAbilityByPlayerAttack(player, cal, specialAbility, entity);
                            }
                        }
                        cal = Math.floor(cal*100)/100.0;
                        double criticalPercent = CraftingEquipStatUtil.getCriticalStat(player)/100.0;
                        if (Math.random() < criticalPercent) {
                            cal = cal * 2;
                            player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "크리티컬");
                        }
                    }
                }
                e.setDamage(cal);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerArrowAttack(EntityDamageByEntityEvent e) {
        if (e.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
            Entity damager = e.getDamager();
            Entity entity = e.getEntity();
            if (damager instanceof Arrow arrow) {
                if (arrow.getShooter() instanceof Player player) {
                    if (e.getFinalDamage() < 0 || Double.compare(e.getFinalDamage(), 0) == 0) {
                        return;
                    }
                    double originalDamage = e.getDamage();
                    double cal = originalDamage + CraftingEquipStatUtil.getProjectileValue(player);
                    if (CraftingEquipStatUtil.hasSpecialAbilityEquipment(player)) {
                        List<SpecialAbility> list = CraftingEquipStatUtil.getContainSpecialAbilities(player);
                        if (!list.isEmpty()) {
                            List<SpecialAbility> highPriorities = new ArrayList<>();
                            for (SpecialAbility specialAbility : list) {
                                if (SpecialAbilityInfo.highPriorityAbilities.contains(specialAbility)) {
                                    highPriorities.add(specialAbility);
                                } else {
                                    cal = CraftingEquipSpecialAbility.applySpecialAbilityByPlayerAttack(player, cal, specialAbility, entity);
                                }
                            }
                            if (!highPriorities.isEmpty()) {
                                for (SpecialAbility specialAbility : highPriorities) {
                                    cal = CraftingEquipSpecialAbility.applySpecialAbilityByPlayerAttack(player, cal, specialAbility, entity);
                                }
                            }
                            cal = Math.floor(cal*100)/100.0;
                            double criticalPercent = CraftingEquipStatUtil.getCriticalStat(player)/100.0;
                            if (Math.random() < criticalPercent) {
                                cal = cal * 2;
                                player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "크리티컬");
                            }
                        }
                    }
                    e.setDamage(cal);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerTakeDamage(EntityDamageByEntityEvent e) {
        Entity entity = e.getEntity();
        if (entity instanceof Player player) {
            double originalDamage = e.getDamage();
            if (defenseEquations != null && expression != null) {
                if (player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
                    PotionEffect potionEffect = player.getPotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                    if (potionEffect != null) {
                        int level = potionEffect.getAmplifier() + 1;
                        originalDamage = originalDamage + (originalDamage * (level * 0.15));
                    }
                }
                double getDefense = CraftingEquipStatUtil.getDefenseValue(player);
                if (Double.compare(getDefense, 0) == 0) {
                    getDefense = 0.001;
                }
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
        if (Math.random() < 0.03) {
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
        if (Math.random() < 0.025) {
            Player player = e.getEntity().getKiller();
            if (player != null) {
                Location location = e.getEntity().getLocation();
                DoppelgangerUtil.spawnDoppelganger(location, player);
            }
        }
    }

    @EventHandler
    public void disableDoppelgangerDrop(EntityDeathEvent e) {
        LivingEntity livingEntity = e.getEntity();
        if (DoppelgangerUtil.isDoppelganger(livingEntity)) {
            e.getDrops().clear();
            World world = livingEntity.getWorld();
            Location location = livingEntity.getLocation();
            int count = 1;
            for (int a = 0; a<5; a++) {
                if (Math.random() < 0.4) {
                    count++;
                }
            }
            for (int a = 0; a<count; a++) {
                ItemStack randomItem = CraftingEquipUtil.createRandomEquipment();
                world.dropItemNaturally(location, randomItem);
            }
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

}