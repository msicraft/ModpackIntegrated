package me.msicraft.modpackintegrated.CraftingEquip.Event;

import me.msicraft.modpackintegrated.CraftingEquip.Enum.SpecialAbility;
import me.msicraft.modpackintegrated.CraftingEquip.Util.CraftingEquipSpecialAbility;
import me.msicraft.modpackintegrated.CraftingEquip.Util.CraftingEquipStatUtil;
import me.msicraft.modpackintegrated.ModPackIntegrated;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CraftingEquipEvent implements Listener {

    private final Random random = new Random();

    private static String defenseEquations = null;
    private static Expression expression = null;

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

    private static final List<SpecialAbility> lowPriorityAbilities = new ArrayList<>(Arrays.asList(SpecialAbility.doubleDamage_5, SpecialAbility.doubleDamage_10,
            SpecialAbility.doubleDamage_15, SpecialAbility.lifeDrain_5_5, SpecialAbility.lifeDrain_5_10, SpecialAbility.lifeDrain_10_5, SpecialAbility.lifeDrain_10_10));

    @EventHandler
    public void onPlayerMeleeAttack(EntityDamageByEntityEvent e) {
        Entity damager = e.getDamager();
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
                                cal = CraftingEquipSpecialAbility.applySpecialAbility(player, cal, specialAbility, e.getEntity().getLocation());
                            }
                        }
                        if (!lowPriorities.isEmpty()) {
                            for (SpecialAbility specialAbility : lowPriorities) {
                                cal = CraftingEquipSpecialAbility.applySpecialAbility(player, cal, specialAbility, e.getEntity().getLocation());
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
                                    cal = CraftingEquipSpecialAbility.applySpecialAbility(player, cal, specialAbility, e.getEntity().getLocation());
                                }
                            }
                            if (!lowPriorities.isEmpty()) {
                                for (SpecialAbility specialAbility : lowPriorities) {
                                    cal = CraftingEquipSpecialAbility.applySpecialAbility(player, cal, specialAbility, e.getEntity().getLocation());
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
    public void onPlayerDamaged(EntityDamageByEntityEvent e) {
        Entity entity = e.getEntity();
        if (entity instanceof Player player) {
            double originalDamage = e.getDamage();
            if (defenseEquations != null && expression != null) {
                double getDefense = CraftingEquipStatUtil.getDefenseValue(player);
                double v = expression.setVariable("DA", originalDamage).setVariable("DE",getDefense).evaluate();
                v = Math.round(v*100.0) / 100.0;
                /*
                if (CraftingEquipStatUtil.hasSpecialAbility(player)) {
                    List<SpecialAbility> list = CraftingEquipStatUtil.getContainSpecialAbilities(player);
                    if (!list.isEmpty()) {
                    }
                }

                 */
                e.setDamage(v);
            }
        }
    }

}
