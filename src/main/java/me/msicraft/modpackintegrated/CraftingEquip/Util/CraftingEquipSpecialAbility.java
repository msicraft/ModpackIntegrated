package me.msicraft.modpackintegrated.CraftingEquip.Util;

import me.msicraft.modpackintegrated.CraftingEquip.Enum.SpecialAbility;
import me.msicraft.modpackintegrated.CraftingEquip.Task.DotDamageTask;
import me.msicraft.modpackintegrated.ModPackIntegrated;
import me.msicraft.modpackintegrated.Util.DamageIndicator;
import me.msicraft.modpackintegrated.Util.MathUtil;
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
        none, doubleDamage, lifeDrain, extraDamage, extraDamagePlayerBaseHealth, extraDamageToDay, extraDamageToNight,
        extraDamageFullHealth, increaseTakeDamageAndExtraDamage, takePlayerBaseHealthDamageAndExtraDamage,
        takeDamageConvertHealth, heal, addDamageRange, extraDamageTargetMaxHealth,
        extraDamageTargetCurrentHealth, increaseMaxHealthAndDecreaseDamage, damageConvertTrueDamage, decreaseTakeAndAttackDamage,
        healBaseMaxHealth, takeDamageConvertDotDamage
    }

    private static final Map<UUID, Map<SpecialAbility, Long>> abilityCoolDown = new HashMap<>();

    public static void removeAbilityMap(Player player) {
        abilityCoolDown.remove(player.getUniqueId());
    }

    public static double applySpecialAbilityByPlayerAttack(Player player, double damage, SpecialAbility specialAbility, Entity entity) {
        double cal = damage;
        double coolDown = SpecialAbilityUtil.calAbilityCoolDown(player, 1);
        abilityEnum abilityEnum = CraftingEquipSpecialAbility.abilityEnum.none;
        Location location = entity.getLocation();
        switch (specialAbility) {
            case doubleDamage_5 -> {
                if (Math.random() < 0.05) {
                    cal = cal * 2;
                    abilityEnum = CraftingEquipSpecialAbility.abilityEnum.doubleDamage;
                }
            }
            case doubleDamage_10 -> {
                if (Math.random() < 0.1) {
                    cal = cal * 2;
                    abilityEnum = CraftingEquipSpecialAbility.abilityEnum.doubleDamage;
                }
            }
            case doubleDamage_15 -> {
                if (Math.random() < 0.15) {
                    cal = cal * 2;
                    abilityEnum = CraftingEquipSpecialAbility.abilityEnum.doubleDamage;
                }
            }
            case lifeDrain_5_25, lifeDrain_10_25 -> {
                if (Math.random() < 0.05) {
                    abilityEnum = CraftingEquipSpecialAbility.abilityEnum.lifeDrain;
                }
            }
            case lifeDrain_5_50, lifeDrain_10_50 -> {
                if (Math.random() < 0.1) {
                    abilityEnum = CraftingEquipSpecialAbility.abilityEnum.lifeDrain;
                }
            }
            case extraDamage_0_2 -> {
                int randomV = random.nextInt(3);
                cal = cal + randomV;
                abilityEnum = CraftingEquipSpecialAbility.abilityEnum.extraDamage;
            }
            case extraDamage_0_4 -> {
                int randomV = random.nextInt(5);
                cal = cal + randomV;
                abilityEnum = CraftingEquipSpecialAbility.abilityEnum.extraDamage;
            }
            case extraDamage_0_6 -> {
                int randomV = random.nextInt(7);
                cal = cal + randomV;
                abilityEnum = CraftingEquipSpecialAbility.abilityEnum.extraDamage;
            }
            case extraDamagePlayerBaseHealth_3 -> {
                double maxHealthCal = player.getMaxHealth() * 0.03;
                cal = cal + maxHealthCal;
                abilityEnum = CraftingEquipSpecialAbility.abilityEnum.extraDamagePlayerBaseHealth;
            }
            case extraDamagePlayerBaseHealth_5 -> {
                double maxHealthCal = player.getMaxHealth() * 0.05;
                cal = cal + maxHealthCal;
                abilityEnum = CraftingEquipSpecialAbility.abilityEnum.extraDamagePlayerBaseHealth;
            }
            case extraDamagePlayerBaseHealth_8 -> {
                double maxHealthCal = player.getMaxHealth() * 0.08;
                cal = cal + maxHealthCal;
                abilityEnum = CraftingEquipSpecialAbility.abilityEnum.extraDamagePlayerBaseHealth;
            }
            case extraDamageToDay_10 -> {
                if (SpecialAbilityUtil.isDay(player)) {
                    cal = cal + (cal * 0.1);
                    abilityEnum = CraftingEquipSpecialAbility.abilityEnum.extraDamageToDay;
                }
            }
            case extraDamageToDay_20 -> {
                if (SpecialAbilityUtil.isDay(player)) {
                    cal = cal + (cal * 0.2);
                    abilityEnum = CraftingEquipSpecialAbility.abilityEnum.extraDamageToDay;
                }
            }
            case extraDamageToNight_10 -> {
                if (SpecialAbilityUtil.isNight(player)) {
                    cal = cal + (cal * 0.1);
                    abilityEnum = CraftingEquipSpecialAbility.abilityEnum.extraDamageToNight;
                }
            }
            case extraDamageToNight_20 -> {
                if (SpecialAbilityUtil.isNight(player)) {
                    cal = cal + (cal * 0.2);
                    abilityEnum = CraftingEquipSpecialAbility.abilityEnum.extraDamageToNight;
                }
            }
            case extraDamageFullHealth_30 -> {
                if (Double.compare(player.getHealth(), player.getMaxHealth()) == 0) {
                    cal = cal + (cal * 0.3);
                    abilityEnum = CraftingEquipSpecialAbility.abilityEnum.extraDamageFullHealth;
                }
            }
            case extraDamageFullHealth_50 -> {
                if (Double.compare(player.getHealth(), player.getMaxHealth()) == 0) {
                    cal = cal + (cal * 0.5);
                    abilityEnum = CraftingEquipSpecialAbility.abilityEnum.extraDamageFullHealth;
                }
            }
            case increaseTakeDamageAndExtraDamage_5_10 -> {
                cal = cal + (cal * 0.1);
                abilityEnum = CraftingEquipSpecialAbility.abilityEnum.increaseTakeDamageAndExtraDamage;
            }
            case increaseTakeDamageAndExtraDamage_10_15 -> {
                cal = cal + (cal * 0.15);
                abilityEnum = CraftingEquipSpecialAbility.abilityEnum.increaseTakeDamageAndExtraDamage;
            }
            case increaseTakeDamageAndExtraDamage_15_20 -> {
                cal = cal + (cal * 0.2);
                abilityEnum = CraftingEquipSpecialAbility.abilityEnum.increaseTakeDamageAndExtraDamage;
            }
            case takePlayerBaseHealthDamageAndExtraDamage_5_20 -> {
                cal = cal + (cal * 0.2);
                abilityEnum = CraftingEquipSpecialAbility.abilityEnum.takePlayerBaseHealthDamageAndExtraDamage;
            }
            case takePlayerBaseHealthDamageAndExtraDamage_5_30 -> {
                cal = cal + (cal * 0.3);
                abilityEnum = CraftingEquipSpecialAbility.abilityEnum.takePlayerBaseHealthDamageAndExtraDamage;
            }
            case heal_1, heal_2, heal_3 -> {
                abilityEnum = CraftingEquipSpecialAbility.abilityEnum.heal;
            }
            case addDamageRange_15 -> {
                double min = cal - (cal * 0.15);
                if (min < 0) {
                    min = 1;
                }
                double max = cal + (cal * 0.15);
                cal = MathUtil.getRandomValueDouble(max, min);
                abilityEnum = CraftingEquipSpecialAbility.abilityEnum.addDamageRange;
            }
            case addDamageRange_30 -> {
                double min = cal - (cal * 0.3);
                if (min < 0) {
                    min = 1;
                }
                double max = cal + (cal * 0.3);
                cal = MathUtil.getRandomValueDouble(max, min);
                abilityEnum = CraftingEquipSpecialAbility.abilityEnum.addDamageRange;
            }
            case extraDamageTargetMaxHealth_2 -> {
                cal = cal + SpecialAbilityUtil.getBaseMaxHealth(entity, 0.02);
                abilityEnum = CraftingEquipSpecialAbility.abilityEnum.extraDamageTargetMaxHealth;
            }
            case extraDamageTargetMaxHealth_4 -> {
                cal = cal + SpecialAbilityUtil.getBaseMaxHealth(entity, 0.04);
                abilityEnum = CraftingEquipSpecialAbility.abilityEnum.extraDamageTargetMaxHealth;
            }
            case extraDamageTargetCurrentHealth_5 -> {
                cal = cal + SpecialAbilityUtil.getBaseCurrentHealth(entity, 0.05);
                abilityEnum = CraftingEquipSpecialAbility.abilityEnum.extraDamageTargetCurrentHealth;
            }
            case extraDamageTargetCurrentHealth_10 -> {
                cal = cal + SpecialAbilityUtil.getBaseCurrentHealth(entity, 0.1);
                abilityEnum = CraftingEquipSpecialAbility.abilityEnum.extraDamageTargetCurrentHealth;
            }
            case increaseMaxHealthAndDecreaseDamage_20_25 -> {
                cal = cal - (cal * 0.25);
                abilityEnum = CraftingEquipSpecialAbility.abilityEnum.increaseMaxHealthAndDecreaseDamage;
            }
            case increaseMaxHealthAndDecreaseDamage_25_30 -> {
                cal = cal - (cal * 0.3);
                abilityEnum = CraftingEquipSpecialAbility.abilityEnum.increaseMaxHealthAndDecreaseDamage;
            }
            case damageConvertTrueDamage_1, damageConvertTrueDamage_10, damageConvertTrueDamage_5 -> {
                abilityEnum = CraftingEquipSpecialAbility.abilityEnum.damageConvertTrueDamage;
            }
            case decreaseTakeAndAttackDamage_5_10 -> {
                cal = cal - (cal * 0.1);
                abilityEnum = CraftingEquipSpecialAbility.abilityEnum.decreaseTakeAndAttackDamage;
            }
            case decreaseTakeAndAttackDamage_10_15 -> {
                cal = cal - (cal * 0.15);
                abilityEnum = CraftingEquipSpecialAbility.abilityEnum.decreaseTakeAndAttackDamage;
            }
            case decreaseTakeAndAttackDamage_15_20 -> {
                cal = cal - (cal * 0.2);
                abilityEnum = CraftingEquipSpecialAbility.abilityEnum.decreaseTakeAndAttackDamage;
            }
            case healBaseMaxHealth_3, healBaseMaxHealth_6, healBaseMaxHealth_10 -> {
                abilityEnum = CraftingEquipSpecialAbility.abilityEnum.healBaseMaxHealth;
            }
        }
        if (abilityEnum == CraftingEquipSpecialAbility.abilityEnum.none) {
            return damage;
        }
        if (abilityCoolDown.containsKey(player.getUniqueId())) {
            Map<SpecialAbility, Long> map = abilityCoolDown.get(player.getUniqueId());
            if (map.containsKey(specialAbility)) {
                if (map.get(specialAbility) > System.currentTimeMillis()) {
                    return damage;
                }
            }
            long dd = (long) (System.currentTimeMillis() + (coolDown * 1000));
            map.put(specialAbility, dd);
        } else {
            Map<SpecialAbility, Long> map = new HashMap<>();
            long dd = (long) (System.currentTimeMillis() + (coolDown * 1000));
            map.put(specialAbility, dd);
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
                    case lifeDrain_5_25, lifeDrain_10_25 -> drainH = cal * 0.25;
                    case lifeDrain_5_50, lifeDrain_10_50 -> drainH = cal * 0.5;
                }
                drainH = Math.round(drainH * 100.0) / 100.0;
                SpecialAbilityUtil.applyLifeDrain(player, drainH);
                //DamageIndicator.spawnLifeStealIndicator(location, drainH);
            }
            case takePlayerBaseHealthDamageAndExtraDamage -> {
                int base = 0;
                switch (specialAbility) {
                    case takePlayerBaseHealthDamageAndExtraDamage_5_20 -> base = 1;
                    case takePlayerBaseHealthDamageAndExtraDamage_5_30 -> base = 3;
                }
                double last = base + Math.ceil(player.getMaxHealth() * 0.05);
                player.damage(last, entity);
                Bukkit.getScheduler().runTask(ModPackIntegrated.getPlugin(), () -> player.setNoDamageTicks(0));
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
            case damageConvertTrueDamage -> {
                double trueDamage = 0;
                switch (specialAbility) {
                    case damageConvertTrueDamage_1 -> trueDamage = SpecialAbilityUtil.getTrueDamage(cal, 0.01);
                    case damageConvertTrueDamage_5 -> trueDamage = SpecialAbilityUtil.getTrueDamage(cal, 0.05);
                    case damageConvertTrueDamage_10 -> trueDamage = SpecialAbilityUtil.getTrueDamage(cal, 0.1);
                }
                cal = cal - trueDamage;
                SpecialAbilityUtil.applyTrueDamage(entity, trueDamage);
            }
            case healBaseMaxHealth -> {
                double v = 0;
                switch (specialAbility) {
                    case healBaseMaxHealth_3 -> v = SpecialAbilityUtil.getPlayerMaxHealthPercent(player, 0.03);
                    case healBaseMaxHealth_6 -> v = SpecialAbilityUtil.getPlayerMaxHealthPercent(player, 0.06);
                    case healBaseMaxHealth_10 -> v = SpecialAbilityUtil.getPlayerMaxHealthPercent(player, 0.1);
                }
                SpecialAbilityUtil.healPlayer(player, v);
            }
        }
        if (cal < 0) {
            cal = 0;
        }
        return cal;
    }

    public static double applySpecialAbilityByTakeDamage(Player player, double takeDamage, SpecialAbility specialAbility) {
        double cal = takeDamage;
        double coolDown = 1;
        abilityEnum abilityEnum = CraftingEquipSpecialAbility.abilityEnum.none;
        switch (specialAbility) {
            case increaseTakeDamageAndExtraDamage_5_10 -> {
                cal = cal + (cal * 0.05);
                abilityEnum = CraftingEquipSpecialAbility.abilityEnum.increaseTakeDamageAndExtraDamage;
                coolDown = 0;
            }
            case increaseTakeDamageAndExtraDamage_10_15 -> {
                cal = cal + (cal * 0.1);
                abilityEnum = CraftingEquipSpecialAbility.abilityEnum.increaseTakeDamageAndExtraDamage;
                coolDown = 0;
            }
            case increaseTakeDamageAndExtraDamage_15_20 -> {
                cal = cal + (cal * 0.15);
                abilityEnum = CraftingEquipSpecialAbility.abilityEnum.increaseTakeDamageAndExtraDamage;
                coolDown = 0;
            }
            case takeDamageConvertHealth_5_25, takeDamageConvertHealth_5_50 -> {
                if (Math.random() < 0.05) {
                    abilityEnum = CraftingEquipSpecialAbility.abilityEnum.takeDamageConvertHealth;
                    coolDown = 2;
                }
            }
            case takeDamageConvertHealth_10_25, takeDamageConvertHealth_10_50 -> {
                if (Math.random() < 0.1) {
                    abilityEnum = CraftingEquipSpecialAbility.abilityEnum.takeDamageConvertHealth;
                    coolDown = 2;
                }
            }
            case decreaseTakeAndAttackDamage_5_10 -> {
                cal = cal - (cal * 0.05);
                abilityEnum = CraftingEquipSpecialAbility.abilityEnum.decreaseTakeAndAttackDamage;
                coolDown = 0.5;
            }
            case decreaseTakeAndAttackDamage_10_15 -> {
                cal = cal - (cal * 0.1);
                abilityEnum = CraftingEquipSpecialAbility.abilityEnum.decreaseTakeAndAttackDamage;
                coolDown = 0.5;
            }
            case decreaseTakeAndAttackDamage_15_20 -> {
                cal = cal - (cal * 0.15);
                abilityEnum = CraftingEquipSpecialAbility.abilityEnum.decreaseTakeAndAttackDamage;
                coolDown = 0.5;
            }
            case takeDamageConvertDotDamage_10_5, takeDamageConvertDotDamage_30_5, takeDamageConvertDotDamage_20_5 -> {
                abilityEnum = CraftingEquipSpecialAbility.abilityEnum.takeDamageConvertDotDamage;
            }
        }
        if (abilityEnum == CraftingEquipSpecialAbility.abilityEnum.none) {
            return takeDamage;
        }
        if (abilityCoolDown.containsKey(player.getUniqueId())) {
            Map<SpecialAbility, Long> map = abilityCoolDown.get(player.getUniqueId());
            if (map.containsKey(specialAbility)) {
                if (map.get(specialAbility) > System.currentTimeMillis()) {
                    return takeDamage;
                }
            }
            long dd = (long) (System.currentTimeMillis() + (coolDown * 1000));
            map.put(specialAbility, dd);
        } else {
            Map<SpecialAbility, Long> map = new HashMap<>();
            long dd = (long) (System.currentTimeMillis() + (coolDown * 1000));
            map.put(specialAbility, dd);
            abilityCoolDown.put(player.getUniqueId(), map);
        }
        switch (abilityEnum) {
            case takeDamageConvertHealth -> {
                double percent = 0;
                switch (specialAbility) {
                    case takeDamageConvertHealth_5_25, takeDamageConvertHealth_10_25 -> percent = 0.25;
                    case takeDamageConvertHealth_5_50, takeDamageConvertHealth_10_50 -> percent = 0.5;
                }
                SpecialAbilityUtil.damageConvertHealth(player, takeDamage, percent);
            }
            case takeDamageConvertDotDamage -> {
                int seconds = 0;
                double dotDamage = 0;
                switch (specialAbility) {
                    case takeDamageConvertDotDamage_10_5 -> {
                        dotDamage = cal * 0.1;
                        cal = cal - dotDamage;
                        seconds = 5;
                    }
                    case takeDamageConvertDotDamage_20_5 -> {
                        dotDamage = cal * 0.2;
                        cal = cal - dotDamage;
                        seconds = 5;
                    }
                    case takeDamageConvertDotDamage_30_5 -> {
                        dotDamage = cal * 0.3;
                        cal = cal - dotDamage;
                        seconds = 5;
                    }
                }
                new DotDamageTask(player, (20 * seconds), dotDamage).runTaskTimer(ModPackIntegrated.getPlugin(), 0, 10);
            }
        }
        if (cal < 0) {
            cal = 0;
        }
        return cal;
    }

}
