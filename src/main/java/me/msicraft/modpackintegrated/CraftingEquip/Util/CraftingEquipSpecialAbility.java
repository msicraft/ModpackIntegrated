package me.msicraft.modpackintegrated.CraftingEquip.Util;

import me.msicraft.modpackintegrated.CraftingEquip.Data.PlayerSpecialAbility;
import me.msicraft.modpackintegrated.CraftingEquip.Enum.SpecialAbility;
import me.msicraft.modpackintegrated.CraftingEquip.Task.DotDamageTask;
import me.msicraft.modpackintegrated.ModPackIntegrated;
import me.msicraft.modpackintegrated.PlayerData.File.PlayerDataFile;
import me.msicraft.modpackintegrated.Util.MathUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Random;

public class CraftingEquipSpecialAbility {

    private static final Random random = new Random();

    public static double applySpecialAbilityByPlayerAttack(Player player, double damage, SpecialAbility specialAbility, Entity entity) {
        double cal = damage;
        Location location = entity.getLocation();
        PlayerSpecialAbility playerSpecialAbility = new PlayerSpecialAbility(player);
        double percent = 0;
        double value = 0;
        switch (specialAbility) {
            case lifeDrain -> {
                percent = playerSpecialAbility.getPercent(specialAbility);
                if (random.nextDouble() < percent) {
                    value =  playerSpecialAbility.getValue(specialAbility);
                    SpecialAbilityUtil.applyLifeDrain(player, (damage * value));
                }
            }
            case extraDamage -> {
                value = playerSpecialAbility.getValue(specialAbility)*100.0;
                double randomV = MathUtil.getRandomValueDouble(value, 0);
                cal = cal + randomV;
            }
            case extraDamagePlayerBaseHealth -> {
                value = playerSpecialAbility.getValue(specialAbility);
                double maxHealthCal = player.getMaxHealth() * value;
                cal = cal + maxHealthCal;
            }
            case extraDamageToDay -> {
                if (SpecialAbilityUtil.isDay(player)) {
                    value = playerSpecialAbility.getValue(specialAbility);
                    cal = cal + (cal * value);
                }
            }
            case extraDamageToNight -> {
                if (SpecialAbilityUtil.isNight(player)) {
                    value = playerSpecialAbility.getValue(specialAbility);
                    cal = cal + (cal * value);
                }
            }
            case increaseTakeDamageAndExtraDamage -> {
                value = playerSpecialAbility.getValue2(specialAbility);
                cal = cal + (cal * value);
            }
            case heal -> {
                value = playerSpecialAbility.getValue(specialAbility) * 100.0;
                SpecialAbilityUtil.healPlayer(player, value);
            }
            case changeDamageToRange -> {
                value = playerSpecialAbility.getValue(specialAbility);
                double minD = cal - (cal * value);
                if (minD < 0) {
                    minD = 0;
                }
                double maxD = cal + (cal * value);
                cal = MathUtil.getRandomValueDouble(maxD, minD);
            }
            case extraDamageTargetMaxHealth -> {
                value = playerSpecialAbility.getValue(specialAbility);
                cal = cal + SpecialAbilityUtil.getBaseMaxHealth(entity, value);
            }
            case extraDamageTargetCurrentHealth -> {
                value = playerSpecialAbility.getValue(specialAbility);
                cal = cal + SpecialAbilityUtil.getBaseCurrentHealth(entity, value);
            }
            case increaseMaxHealthAndDecreaseDamage, decreaseTakeAndAttackDamage -> {
                value = playerSpecialAbility.getValue2(specialAbility);
                cal = cal - (cal * value);
            }
            case damageConvertTrueDamage -> {
                value = playerSpecialAbility.getValue(specialAbility);
                double trueDamage = SpecialAbilityUtil.getTrueDamage(cal, value);
                cal = cal - trueDamage;
                SpecialAbilityUtil.applyTrueDamage(entity, trueDamage);
            }
            case healBaseMaxHealth -> {
                value = playerSpecialAbility.getValue(specialAbility);
                double healV = SpecialAbilityUtil.getPlayerMaxHealthPercent(player, value);
                SpecialAbilityUtil.healPlayer(player, healV);
            }
            case extraBackAttackDamage -> {
                if (SpecialAbilityUtil.isBackAttack(player, entity)) {
                    value = playerSpecialAbility.getValue(specialAbility);
                    cal = cal + (cal * value);
                    PlayerDataFile dataFile = new PlayerDataFile(player);
                    boolean displayBackAttack = dataFile.getConfig().contains("Option.DisplayBackAttack") && dataFile.getConfig().getBoolean("Option.DisplayBackAttack");
                    if (displayBackAttack) {
                        player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "백어택");
                    }
                }
            }
        }
        if (cal < 0) {
            cal = 0;
        }
        return cal;
    }

    public static double applySpecialAbilityByTakeDamage(Player player, double takeDamage, SpecialAbility specialAbility) {
        double cal = takeDamage;
        PlayerSpecialAbility playerSpecialAbility = new PlayerSpecialAbility(player);
        double percent = 0;
        double value = 0;
        switch (specialAbility) {
            case increaseTakeDamageAndExtraDamage -> {
                value = playerSpecialAbility.getValue1(specialAbility);
                cal = cal + (cal * value);
            }
            case decreaseTakeAndAttackDamage -> {
                value = playerSpecialAbility.getValue1(specialAbility);
                cal = cal - (cal * value);
            }
            case takeDamageConvertDotDamage -> {
                value = playerSpecialAbility.getValue1(specialAbility);
                double dotDamage = cal * value;
                cal = cal - dotDamage;
                new DotDamageTask(player, (20 * 5), dotDamage).runTaskTimer(ModPackIntegrated.getPlugin(), 0, 10L);
            }
        }
        if (cal < 0) {
            cal = 0;
        }
        return cal;
    }

}
