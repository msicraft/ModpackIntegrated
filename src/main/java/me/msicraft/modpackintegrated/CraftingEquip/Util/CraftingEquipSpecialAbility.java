package me.msicraft.modpackintegrated.CraftingEquip.Util;

import me.msicraft.modpackintegrated.CraftingEquip.DamageIndicator;
import me.msicraft.modpackintegrated.CraftingEquip.Enum.SpecialAbility;
import me.msicraft.modpackintegrated.ModPackIntegrated;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class CraftingEquipSpecialAbility {

    private static final Random random = new Random();
    private enum abilityEnum {
        none,doubleDamage, lifeDrain,extraDamage,extraDamagePlayerBaseHealth,extraDamageToDay,extraDamageToNight,extraDamageFullHealth
        ,increaseTakeDamageAndExtraDamage,takePlayerBaseHealthDamageAndExtraDamage
    }

    private static final Map<UUID, Map<abilityEnum, Long>> abilityCoolDown = new HashMap<>();

    public static void removeAbilityMap(Player player) { abilityCoolDown.remove(player.getUniqueId()); }

    public static double applySpecialAbilityByPlayerAttack(Player player, double damage, SpecialAbility specialAbility, Entity entity) {
        double cal = damage;
        double coolDown = 0;
        abilityEnum abilityEnum = CraftingEquipSpecialAbility.abilityEnum.none;
        Location location = entity.getLocation();
        switch (specialAbility) {
            case doubleDamage_5 -> {
                if (Math.random() < 0.05) {
                    cal = cal * 2;
                    abilityEnum = CraftingEquipSpecialAbility.abilityEnum.doubleDamage;
                    coolDown = SpecialAbilityCoolDown.doubleDamage_5;
                }
            }
            case doubleDamage_10 -> {
                if (Math.random() < 0.1) {
                    cal = cal * 2;
                    abilityEnum = CraftingEquipSpecialAbility.abilityEnum.doubleDamage;
                    coolDown = SpecialAbilityCoolDown.doubleDamage_10;
                }
            }
            case doubleDamage_15 -> {
                if (Math.random() < 0.15) {
                    cal = cal * 2;
                    abilityEnum = CraftingEquipSpecialAbility.abilityEnum.doubleDamage;
                    coolDown = SpecialAbilityCoolDown.doubleDamage_15;
                }
            }
            case lifeDrain_5_5 -> {
                if (Math.random() < 0.05) {
                    abilityEnum = CraftingEquipSpecialAbility.abilityEnum.lifeDrain;
                    coolDown = SpecialAbilityCoolDown.lifeDrain_5_5;
                }
            }
            case lifeDrain_5_10 -> {
                if (Math.random() < 0.1) {
                    abilityEnum = CraftingEquipSpecialAbility.abilityEnum.lifeDrain;
                    coolDown = SpecialAbilityCoolDown.lifeDrain_5_10;
                }
            }
            case lifeDrain_10_5 -> {
                if (Math.random() < 0.05) {
                    abilityEnum = CraftingEquipSpecialAbility.abilityEnum.lifeDrain;
                    coolDown = SpecialAbilityCoolDown.lifeDrain_10_5;
                }
            }
            case lifeDrain_10_10 -> {
                if (Math.random() < 0.1) {
                    abilityEnum = CraftingEquipSpecialAbility.abilityEnum.lifeDrain;
                    coolDown = SpecialAbilityCoolDown.lifeDrain_10_10;
                }
            }
            case extraDamage_0_2 -> {
                int randomV = random.nextInt(3);
                cal = cal + randomV;
                abilityEnum = CraftingEquipSpecialAbility.abilityEnum.extraDamage;
                coolDown = SpecialAbilityCoolDown.extraDamage_0_2;
            }
            case extraDamage_0_4 -> {
                int randomV = random.nextInt(5);
                cal = cal + randomV;
                abilityEnum = CraftingEquipSpecialAbility.abilityEnum.extraDamage;
                coolDown = SpecialAbilityCoolDown.extraDamage_0_4;
            }
            case extraDamage_0_6 -> {
                int randomV = random.nextInt(7);
                cal = cal + randomV;
                abilityEnum = CraftingEquipSpecialAbility.abilityEnum.extraDamage;
                coolDown = SpecialAbilityCoolDown.extraDamage_0_6;
            }
            case extraDamagePlayerBaseHealth_1 -> {
                double maxHealthCal = player.getMaxHealth() * 0.01;
                cal = cal + maxHealthCal;
                abilityEnum = CraftingEquipSpecialAbility.abilityEnum.extraDamagePlayerBaseHealth;
                coolDown = SpecialAbilityCoolDown.extraDamagePlayerBaseHealth_1;
            }
            case extraDamagePlayerBaseHealth_3 -> {
                double maxHealthCal = player.getMaxHealth() * 0.03;
                cal = cal + maxHealthCal;
                abilityEnum = CraftingEquipSpecialAbility.abilityEnum.extraDamagePlayerBaseHealth;
                coolDown = SpecialAbilityCoolDown.extraDamagePlayerBaseHealth_3;
            }
            case extraDamagePlayerBaseHealth_5 -> {
                double maxHealthCal = player.getMaxHealth() * 0.05;
                cal = cal + maxHealthCal;
                abilityEnum = CraftingEquipSpecialAbility.abilityEnum.extraDamagePlayerBaseHealth;
                coolDown = SpecialAbilityCoolDown.extraDamagePlayerBaseHealth_5;
            }
            case extraDamageToDay_5 -> {
                if (SpecialAbilityUtil.isDay(player)) {
                    cal = cal + (cal * 0.05);
                    abilityEnum = CraftingEquipSpecialAbility.abilityEnum.extraDamageToDay;
                    coolDown = SpecialAbilityCoolDown.extraDamageToDay_5;
                }
            }
            case extraDamageToDay_10 -> {
                if (SpecialAbilityUtil.isDay(player)) {
                    cal = cal + (cal * 0.1);
                    abilityEnum = CraftingEquipSpecialAbility.abilityEnum.extraDamageToDay;
                    coolDown = SpecialAbilityCoolDown.extraDamageToDay_10;
                }
            }
            case extraDamageToNight_5 -> {
                if (SpecialAbilityUtil.isNight(player)) {
                    cal = cal + (cal * 0.05);
                    abilityEnum = CraftingEquipSpecialAbility.abilityEnum.extraDamageToNight;
                    coolDown = SpecialAbilityCoolDown.extraDamageToNight_5;
                }
            }
            case extraDamageToNight_10 -> {
                if (SpecialAbilityUtil.isNight(player)) {
                    cal = cal + (cal * 0.1);
                    abilityEnum = CraftingEquipSpecialAbility.abilityEnum.extraDamageToNight;
                    coolDown = SpecialAbilityCoolDown.extraDamageToNight_10;
                }
            }
            case extraDamageFullHealth_20 -> {
                if (Double.compare(player.getHealth(), player.getMaxHealth()) == 0) {
                    cal = cal + (cal * 0.2);
                    abilityEnum = CraftingEquipSpecialAbility.abilityEnum.extraDamageFullHealth;
                    coolDown = SpecialAbilityCoolDown.extraDamageFullHealth_20;
                }
            }
            case extraDamageFullHealth_25 -> {
                if (Double.compare(player.getHealth(), player.getMaxHealth()) == 0) {
                    cal = cal + (cal * 0.25);
                    abilityEnum = CraftingEquipSpecialAbility.abilityEnum.extraDamageFullHealth;
                    coolDown = SpecialAbilityCoolDown.extraDamageFullHealth_25;
                }
            }
            case increaseTakeDamageAndExtraDamage_5_5 -> {
                cal = cal + (cal * 0.05);
                abilityEnum = CraftingEquipSpecialAbility.abilityEnum.increaseTakeDamageAndExtraDamage;
                coolDown = SpecialAbilityCoolDown.increaseTakeDamageAndExtraDamage_5_5;
            }
            case increaseTakeDamageAndExtraDamage_10_10 -> {
                cal = cal + (cal * 0.1);
                abilityEnum = CraftingEquipSpecialAbility.abilityEnum.increaseTakeDamageAndExtraDamage;
                coolDown = SpecialAbilityCoolDown.increaseTakeDamageAndExtraDamage_10_10;
            }
            case takePlayerBaseHealthDamageAndExtraDamage_5_20 -> {
                cal = cal + (cal * 0.2);
                abilityEnum = CraftingEquipSpecialAbility.abilityEnum.takePlayerBaseHealthDamageAndExtraDamage;
                coolDown = SpecialAbilityCoolDown.takePlayerBaseHealthDamageAndExtraDamage_5_20;
            }
            case takePlayerBaseHealthDamageAndExtraDamage_5_25 -> {
                cal = cal + (cal * 0.25);
                abilityEnum = CraftingEquipSpecialAbility.abilityEnum.takePlayerBaseHealthDamageAndExtraDamage;
                coolDown = SpecialAbilityCoolDown.takePlayerBaseHealthDamageAndExtraDamage_5_25;
            }
        }
        if (abilityEnum == CraftingEquipSpecialAbility.abilityEnum.none) {
            return damage;
        }
        if (abilityCoolDown.containsKey(player.getUniqueId())) {
            Map<abilityEnum, Long> map = abilityCoolDown.get(player.getUniqueId());
            if (map.containsKey(abilityEnum)) {
                if (map.get(abilityEnum) > System.currentTimeMillis()) {
                    return damage;
                }
            }
            long dd = (long) (System.currentTimeMillis() + (coolDown * 1000));
            map.put(abilityEnum, dd);
        } else {
            Map<abilityEnum, Long> map = new HashMap<>();
            long dd = (long) (System.currentTimeMillis() + (coolDown * 1000));
            map.put(abilityEnum, dd);
            abilityCoolDown.put(player.getUniqueId(), map);
        }
        switch (abilityEnum) {
            case doubleDamage -> {
                DamageIndicator.spawnCriticalIndicator(location, cal);
                player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "크리티컬");
            }
            case lifeDrain -> {
                double drainH;
                switch (specialAbility) {
                    case lifeDrain_5_5,lifeDrain_10_5 -> {

                    }
                }
                if (specialAbility == SpecialAbility.lifeDrain_5_5 || specialAbility == SpecialAbility.lifeDrain_5_10) {
                    drainH = cal * 0.05;
                } else {
                    drainH = cal * 0.1;
                }
                drainH = Math.round(drainH*100.0)/100.0;
                SpecialAbilityUtil.applyLifeDrain(player, drainH);
                DamageIndicator.spawnLifeStealIndicator(location, drainH);
            }
            case takePlayerBaseHealthDamageAndExtraDamage -> {
                int base = 0;
                switch (specialAbility) {
                    case takePlayerBaseHealthDamageAndExtraDamage_5_20 -> base = 1;
                    case takePlayerBaseHealthDamageAndExtraDamage_5_25 -> base = 2;
                }
                double last = base + Math.ceil(player.getMaxHealth() * 0.05);
                player.damage(last, entity);
                Bukkit.getScheduler().runTask(ModPackIntegrated.getPlugin(), ()-> {
                    player.setNoDamageTicks(1);
                });
            }
        }
        return cal;
    }

    public static double applySpecialAbilityByTakeDamage(Player player, double takeDamage, SpecialAbility specialAbility) {
        double cal = takeDamage;
        switch (specialAbility) {
            case increaseTakeDamageAndExtraDamage_5_5 -> {
                cal = takeDamage + (takeDamage * 0.05);
            }
            case increaseTakeDamageAndExtraDamage_10_10 -> {
                cal = takeDamage + (takeDamage * 0.1);
            }
        }
        return cal;
    }

}
