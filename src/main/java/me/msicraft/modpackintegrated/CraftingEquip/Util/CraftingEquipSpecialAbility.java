package me.msicraft.modpackintegrated.CraftingEquip.Util;

import me.msicraft.modpackintegrated.CraftingEquip.Enum.SpecialAbility;
import me.msicraft.modpackintegrated.ModPackIntegrated;
import me.msicraft.modpackintegrated.Util.DamageIndicator;
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
        ,increaseTakeDamageAndExtraDamage,takePlayerBaseHealthDamageAndExtraDamage,takeDamageConvertHealth,heal,addDamageRange,gamblingDamage
        ,lottoIncreaseDamageOrHalfHealth,extraDamageTargetMaxHealth,extraDamageTargetCurrentHealth
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
            case lifeDrain_5_25 -> {
                if (Math.random() < 0.05) {
                    abilityEnum = CraftingEquipSpecialAbility.abilityEnum.lifeDrain;
                    coolDown = SpecialAbilityCoolDown.lifeDrain_5_25;
                }
            }
            case lifeDrain_5_50 -> {
                if (Math.random() < 0.1) {
                    abilityEnum = CraftingEquipSpecialAbility.abilityEnum.lifeDrain;
                    coolDown = SpecialAbilityCoolDown.lifeDrain_5_50;
                }
            }
            case lifeDrain_10_25 -> {
                if (Math.random() < 0.05) {
                    abilityEnum = CraftingEquipSpecialAbility.abilityEnum.lifeDrain;
                    coolDown = SpecialAbilityCoolDown.lifeDrain_10_25;
                }
            }
            case lifeDrain_10_50 -> {
                if (Math.random() < 0.1) {
                    abilityEnum = CraftingEquipSpecialAbility.abilityEnum.lifeDrain;
                    coolDown = SpecialAbilityCoolDown.lifeDrain_10_50;
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
            case extraDamagePlayerBaseHealth_8 -> {
                double maxHealthCal = player.getMaxHealth() * 0.08;
                cal = cal + maxHealthCal;
                abilityEnum = CraftingEquipSpecialAbility.abilityEnum.extraDamagePlayerBaseHealth;
                coolDown = SpecialAbilityCoolDown.extraDamagePlayerBaseHealth_8;
            }
            case extraDamageToDay_10 -> {
                if (SpecialAbilityUtil.isDay(player)) {
                    cal = cal + (cal * 0.1);
                    abilityEnum = CraftingEquipSpecialAbility.abilityEnum.extraDamageToDay;
                    coolDown = SpecialAbilityCoolDown.extraDamageToDay_10;
                }
            }
            case extraDamageToDay_20 -> {
                if (SpecialAbilityUtil.isDay(player)) {
                    cal = cal + (cal * 0.2);
                    abilityEnum = CraftingEquipSpecialAbility.abilityEnum.extraDamageToDay;
                    coolDown = SpecialAbilityCoolDown.extraDamageToDay_20;
                }
            }
            case extraDamageToNight_10 -> {
                if (SpecialAbilityUtil.isNight(player)) {
                    cal = cal + (cal * 0.1);
                    abilityEnum = CraftingEquipSpecialAbility.abilityEnum.extraDamageToNight;
                    coolDown = SpecialAbilityCoolDown.extraDamageToNight_10;
                }
            }
            case extraDamageToNight_20 -> {
                if (SpecialAbilityUtil.isNight(player)) {
                    cal = cal + (cal * 0.2);
                    abilityEnum = CraftingEquipSpecialAbility.abilityEnum.extraDamageToNight;
                    coolDown = SpecialAbilityCoolDown.extraDamageToNight_20;
                }
            }
            case extraDamageFullHealth_30 -> {
                if (Double.compare(player.getHealth(), player.getMaxHealth()) == 0) {
                    cal = cal + (cal * 0.3);
                    abilityEnum = CraftingEquipSpecialAbility.abilityEnum.extraDamageFullHealth;
                    coolDown = SpecialAbilityCoolDown.extraDamageFullHealth_30;
                }
            }
            case extraDamageFullHealth_50 -> {
                if (Double.compare(player.getHealth(), player.getMaxHealth()) == 0) {
                    cal = cal + (cal * 0.5);
                    abilityEnum = CraftingEquipSpecialAbility.abilityEnum.extraDamageFullHealth;
                    coolDown = SpecialAbilityCoolDown.extraDamageFullHealth_50;
                }
            }
            case increaseTakeDamageAndExtraDamage_5_10 -> {
                cal = cal + (cal * 0.1);
                abilityEnum = CraftingEquipSpecialAbility.abilityEnum.increaseTakeDamageAndExtraDamage;
                coolDown = SpecialAbilityCoolDown.increaseTakeDamageAndExtraDamage_5_10;
            }
            case increaseTakeDamageAndExtraDamage_10_15 -> {
                cal = cal + (cal * 0.15);
                abilityEnum = CraftingEquipSpecialAbility.abilityEnum.increaseTakeDamageAndExtraDamage;
                coolDown = SpecialAbilityCoolDown.increaseTakeDamageAndExtraDamage_10_15;
            }
            case takePlayerBaseHealthDamageAndExtraDamage_5_20 -> {
                cal = cal + (cal * 0.2);
                abilityEnum = CraftingEquipSpecialAbility.abilityEnum.takePlayerBaseHealthDamageAndExtraDamage;
                coolDown = SpecialAbilityCoolDown.takePlayerBaseHealthDamageAndExtraDamage_5_20;
            }
            case takePlayerBaseHealthDamageAndExtraDamage_5_30 -> {
                cal = cal + (cal * 0.3);
                abilityEnum = CraftingEquipSpecialAbility.abilityEnum.takePlayerBaseHealthDamageAndExtraDamage;
                coolDown = SpecialAbilityCoolDown.takePlayerBaseHealthDamageAndExtraDamage_5_30;
            }
            case heal_1,heal_2,heal_3 -> {
                abilityEnum = CraftingEquipSpecialAbility.abilityEnum.heal;
                coolDown = SpecialAbilityCoolDown.heal_2;
            }
            case addDamageRange_15 -> {
                double min = cal - (cal * 0.15);
                if (min < 0) {
                    min = 1;
                }
                double max = cal + (cal * 0.15);
                cal = SpecialAbilityUtil.getRandomValueDouble(max, min);
                abilityEnum = CraftingEquipSpecialAbility.abilityEnum.addDamageRange;
                coolDown = SpecialAbilityCoolDown.addDamageRange_15;
            }
            case addDamageRange_30 -> {
                double min = cal - (cal * 0.3);
                if (min < 0) {
                    min = 1;
                }
                double max = cal + (cal * 0.3);
                cal = SpecialAbilityUtil.getRandomValueDouble(max, min);
                abilityEnum = CraftingEquipSpecialAbility.abilityEnum.addDamageRange;
                coolDown = SpecialAbilityCoolDown.addDamageRange_30;
            }
            case gamblingDamage_50_30 -> {
                if (Math.random() < 0.5) {
                    cal = 0;
                } else {
                    cal = cal + (cal * 0.3);
                }
                abilityEnum = CraftingEquipSpecialAbility.abilityEnum.gamblingDamage;
                coolDown = SpecialAbilityCoolDown.gamblingDamage_50_30;
            }
            case gamblingDamage_50_50 -> {
                if (Math.random() < 0.5) {
                    cal = 0;
                } else {
                    cal = cal + (cal * 0.5);
                }
                abilityEnum = CraftingEquipSpecialAbility.abilityEnum.gamblingDamage;
                coolDown = SpecialAbilityCoolDown.gamblingDamage_50_50;
            }
            case lottoIncreaseDamageOrHalfHealth_10_15 -> {
                cal = cal + (cal * 0.15);
                if (Math.random() < 0.1) {
                    SpecialAbilityUtil.applyHalfHealth(player);
                }
                abilityEnum = CraftingEquipSpecialAbility.abilityEnum.lottoIncreaseDamageOrHalfHealth;
                coolDown = SpecialAbilityCoolDown.lottoIncreaseDamageOrHalfHealth_10_15;
            }
            case lottoIncreaseDamageOrHalfHealth_15_30 -> {
                cal = cal + (cal * 0.3);
                if (Math.random() < 0.15) {
                    SpecialAbilityUtil.applyHalfHealth(player);
                }
                abilityEnum = CraftingEquipSpecialAbility.abilityEnum.lottoIncreaseDamageOrHalfHealth;
                coolDown = SpecialAbilityCoolDown.lottoIncreaseDamageOrHalfHealth_15_30;
            }
            case extraDamageTargetMaxHealth_2 -> {
                cal = cal + SpecialAbilityUtil.getBaseMaxHealth(entity, 0.02);
                abilityEnum = CraftingEquipSpecialAbility.abilityEnum.extraDamageTargetMaxHealth;
                coolDown = SpecialAbilityCoolDown.extraDamageTargetMaxHealth_2;
            }
            case extraDamageTargetMaxHealth_4 -> {
                cal = cal + SpecialAbilityUtil.getBaseMaxHealth(entity, 0.04);
                abilityEnum = CraftingEquipSpecialAbility.abilityEnum.extraDamageTargetMaxHealth;
                coolDown = SpecialAbilityCoolDown.extraDamageTargetMaxHealth_4;
            }
            case extraDamageTargetCurrentHealth_5 -> {
                cal = cal + SpecialAbilityUtil.getBaseCurrentHealth(entity, 0.05);
                abilityEnum = CraftingEquipSpecialAbility.abilityEnum.extraDamageTargetCurrentHealth;
                coolDown = SpecialAbilityCoolDown.extraDamageTargetCurrentHealth_5;
            }
            case extraDamageTargetCurrentHealth_10 -> {
                cal = cal + SpecialAbilityUtil.getBaseCurrentHealth(entity, 0.1);
                abilityEnum = CraftingEquipSpecialAbility.abilityEnum.extraDamageTargetCurrentHealth;
                coolDown = SpecialAbilityCoolDown.extraDamageTargetCurrentHealth_10;
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
                double drainH = 0;
                switch (specialAbility) {
                    case lifeDrain_5_25,lifeDrain_10_25 -> drainH = cal * 0.25;
                    case lifeDrain_5_50,lifeDrain_10_50 -> drainH = cal * 0.5;
                }
                drainH = Math.round(drainH*100.0)/100.0;
                SpecialAbilityUtil.applyLifeDrain(player, drainH);
                DamageIndicator.spawnLifeStealIndicator(location, drainH);
            }
            case takePlayerBaseHealthDamageAndExtraDamage -> {
                int base = 0;
                switch (specialAbility) {
                    case takePlayerBaseHealthDamageAndExtraDamage_5_20 -> base = 1;
                    case takePlayerBaseHealthDamageAndExtraDamage_5_30 -> base = 3;
                }
                double last = base + Math.ceil(player.getMaxHealth() * 0.05);
                player.damage(last, entity);
                Bukkit.getScheduler().runTask(ModPackIntegrated.getPlugin(), ()-> {
                    player.setNoDamageTicks(0);
                });
            }
            case heal -> {
                int amount = 0;
                switch (specialAbility) {
                    case heal_1 -> amount = 1;
                    case heal_2 -> amount = 2;
                    case heal_3 -> amount = 3;
                }
                SpecialAbilityUtil.healPlayer(player, amount);
            }
        }
        return cal;
    }

    public static double applySpecialAbilityByTakeDamage(Player player, double takeDamage, SpecialAbility specialAbility) {
        double cal = takeDamage;
        double coolDown = 0;
        abilityEnum abilityEnum = CraftingEquipSpecialAbility.abilityEnum.none;
        switch (specialAbility) {
            case increaseTakeDamageAndExtraDamage_5_10 -> {
                cal = takeDamage + (takeDamage * 0.05);
                abilityEnum = CraftingEquipSpecialAbility.abilityEnum.increaseTakeDamageAndExtraDamage;
            }
            case increaseTakeDamageAndExtraDamage_10_15 -> {
                cal = takeDamage + (takeDamage * 0.1);
                abilityEnum = CraftingEquipSpecialAbility.abilityEnum.increaseTakeDamageAndExtraDamage;
            }
            case takeDamageConvertHealth_5_25 -> {
                if (Math.random() < 0.05) {
                    abilityEnum = CraftingEquipSpecialAbility.abilityEnum.takeDamageConvertHealth;
                    coolDown = SpecialAbilityCoolDown.takeDamageConvertHealth_5_25;
                }
            }
            case takeDamageConvertHealth_5_50 -> {
                if (Math.random() < 0.05) {
                    abilityEnum = CraftingEquipSpecialAbility.abilityEnum.takeDamageConvertHealth;
                    coolDown = SpecialAbilityCoolDown.takeDamageConvertHealth_5_50;
                }
            }
            case takeDamageConvertHealth_10_25 -> {
                if (Math.random() < 0.1) {
                    abilityEnum = CraftingEquipSpecialAbility.abilityEnum.takeDamageConvertHealth;
                    coolDown = SpecialAbilityCoolDown.takeDamageConvertHealth_10_25;
                }
            }
            case takeDamageConvertHealth_10_50 -> {
                if (Math.random() < 0.1) {
                    abilityEnum = CraftingEquipSpecialAbility.abilityEnum.takeDamageConvertHealth;
                    coolDown = SpecialAbilityCoolDown.takeDamageConvertHealth_10_50;
                }
            }
        }
        if (abilityEnum == CraftingEquipSpecialAbility.abilityEnum.none) {
            return takeDamage;
        }
        if (abilityCoolDown.containsKey(player.getUniqueId())) {
            Map<abilityEnum, Long> map = abilityCoolDown.get(player.getUniqueId());
            if (map.containsKey(abilityEnum)) {
                if (map.get(abilityEnum) > System.currentTimeMillis()) {
                    return takeDamage;
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
            case takeDamageConvertHealth -> {
                double percent = 0;
                switch (specialAbility) {
                    case takeDamageConvertHealth_5_25, takeDamageConvertHealth_10_25 -> {
                        percent = 0.25;
                        SpecialAbilityUtil.damageConvertHealth(player, takeDamage, percent);
                    }
                    case takeDamageConvertHealth_5_50,takeDamageConvertHealth_10_50 -> {
                        percent = 0.5;
                        SpecialAbilityUtil.damageConvertHealth(player, takeDamage, percent);
                    }
                }
            }
        }
        return cal;
    }

}
