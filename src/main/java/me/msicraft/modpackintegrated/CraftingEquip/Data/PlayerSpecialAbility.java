package me.msicraft.modpackintegrated.CraftingEquip.Data;

import me.msicraft.modpackintegrated.CraftingEquip.Enum.SpecialAbility;
import me.msicraft.modpackintegrated.CraftingEquip.Util.CraftingEquipStatUtil;
import me.msicraft.modpackintegrated.CraftingEquip.Util.SpecialAbilityInfo;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class PlayerSpecialAbility {

    private Player player;
    private final Map<SpecialAbility, Double> percentMap = new HashMap<>();
    private final Map<SpecialAbility, Double> valueMap = new HashMap<>();
    private final Map<SpecialAbility, String> multiValueMap = new HashMap<>();

    private int extraPercentAttackSpeed, extraPercentMovementSpeed, extraPercentHealth, extraPercentArmor, extraPercentArmorToughness;
    private double extraFlatAttackSpeed, extraFlatMovementSpeed, extraFlatHealth, extraFlatArmor, extraFlatArmorToughness;

    public PlayerSpecialAbility(Player player) {
        this.player = player;
        double percentAttackSpeed = 0, percentMovementSpeed = 0, percentHealth = 0, percentArmor = 0, percentArmorToughness = 0, percentDefense = 0;
        double flatAttackSpeed = 0, flatMovementSpeed = 0, flatHealth = 0, flatArmor = 0, flatArmorToughness = 0, flatDefense = 0;
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack itemStack = player.getInventory().getItem(slot);
            if (itemStack != null && CraftingEquipStatUtil.checkEqualEquipmentTypeToSlot(itemStack, slot)) {
                SpecialAbility specialAbility = CraftingEquipStatUtil.getSpecialAbility(itemStack);
                if (specialAbility != null) {
                    switch (specialAbility) {
                        case extraAttackSpeed -> {
                            double v = SpecialAbilityInfo.getValue(itemStack);
                            percentAttackSpeed = percentAttackSpeed + v;
                        }
                        case extraMovementSpeed -> {
                            double v = SpecialAbilityInfo.getValue(itemStack);
                            percentMovementSpeed = percentMovementSpeed + v;
                        }
                        case increaseMaxHealth -> {
                            double v = SpecialAbilityInfo.getValue(itemStack);
                            percentHealth = percentHealth + v;
                        }
                        case lifeDrain -> {
                            double getP = percentMap.containsKey(specialAbility) ? percentMap.get(specialAbility) : 0;
                            double getV = valueMap.containsKey(specialAbility) ? valueMap.get(specialAbility) : 0;
                            getP = getP + SpecialAbilityInfo.getPercent(itemStack);
                            getV = getV + SpecialAbilityInfo.getValue(itemStack);
                            percentMap.put(specialAbility, getP);
                            valueMap.put(specialAbility, getV);
                        }
                        case extraDamage, extraDamagePlayerBaseHealth, extraDamageToDay, extraDamageToNight, heal
                                ,changeDamageToRange, extraDamageTargetMaxHealth, extraDamageTargetCurrentHealth, damageConvertTrueDamage, healBaseMaxHealth
                                , takeDamageConvertDotDamage-> {
                            double getV = valueMap.containsKey(specialAbility) ?  valueMap.get(specialAbility) : 0;
                            getV = getV + SpecialAbilityInfo.getValue(itemStack);
                            valueMap.put(specialAbility, getV);
                        }
                        case increaseTakeDamageAndExtraDamage,increaseMaxHealthAndDecreaseDamage,decreaseTakeAndAttackDamage -> {
                            String s = multiValueMap.getOrDefault(specialAbility, "0:0");
                            String[] a = s.split(":");
                            double v1 = Double.parseDouble(a[0]);
                            double v2 = Double.parseDouble(a[1]);
                            double value1 = SpecialAbilityInfo.getValue_1(itemStack);
                            double value2 = SpecialAbilityInfo.getValue_2(itemStack);
                            String c = (v1 + value1) + ":" + (v2 + value2);
                            multiValueMap.put(specialAbility, c);
                            if (specialAbility == SpecialAbility.increaseMaxHealthAndDecreaseDamage) {
                                double v = SpecialAbilityInfo.getValue_1(itemStack);
                                percentHealth = percentHealth + v;
                            }
                        }
                    }
                }
            }
        }
        extraPercentAttackSpeed = (int) (Math.floor(percentAttackSpeed * 100.0) / 100.0);
        extraPercentMovementSpeed = (int) (Math.floor(percentMovementSpeed * 100.0) / 100.0);
        extraPercentHealth = (int) (Math.floor(percentHealth * 100.0) / 100.0);
        extraPercentArmor = (int) (Math.floor(percentArmor * 100.0) / 100.0);
        extraPercentArmorToughness = (int) (Math.floor(percentArmorToughness * 100.0) / 100.0);
        extraFlatAttackSpeed = Math.round(flatAttackSpeed * 100.0) / 100.0;
        extraFlatMovementSpeed = Math.round(flatMovementSpeed * 100.0) / 100.0;
        extraFlatHealth = Math.round(flatHealth * 100.0) / 100.0;
        extraFlatArmor = Math.round(flatArmor * 100.0) / 100.0;
        extraFlatArmorToughness = Math.round(flatArmorToughness * 100.0) / 100.0;
    }

    public Player getPlayer() {
        return player;
    }

    public double getPercent(SpecialAbility specialAbility) {
        double percent = 0 ;
        if (percentMap.containsKey(specialAbility)) {
            percent = percentMap.get(specialAbility) / 100.0;
        }
        return percent;
    }

    public double getValue(SpecialAbility specialAbility) {
        double v = 0;
        if (valueMap.containsKey(specialAbility)) {
            v = valueMap.get(specialAbility) / 100.0;
        }
        return v;
    }

    public double getValue1(SpecialAbility specialAbility) {
        double v1 = 0;
        if (multiValueMap.containsKey(specialAbility)) {
            String s = multiValueMap.get(specialAbility);
            String[] a = s.split(":");
            v1 = Double.parseDouble(a[0]) / 100.0;
        }
        return v1;
    }

    public double getValue2(SpecialAbility specialAbility) {
        double v2 = 0;
        if (multiValueMap.containsKey(specialAbility)) {
            String s = multiValueMap.get(specialAbility);
            String[] a = s.split(":");
            v2 = Double.parseDouble(a[1]) / 100.0;
        }
        return v2;
    }

    public int getExtraPercentAttackSpeed() {
        return extraPercentAttackSpeed;
    }

    public int getExtraPercentMovementSpeed() {
        return extraPercentMovementSpeed;
    }

    public int getExtraPercentHealth() {
        return extraPercentHealth;
    }

    public int getExtraPercentArmor() {
        return extraPercentArmor;
    }

    public int getExtraPercentArmorToughness() {
        return extraPercentArmorToughness;
    }

    public double getExtraFlatAttackSpeed() {
        return extraFlatAttackSpeed;
    }

    public double getExtraFlatMovementSpeed() {
        return extraFlatMovementSpeed;
    }

    public double getExtraFlatHealth() {
        return extraFlatHealth;
    }

    public double getExtraFlatArmor() {
        return extraFlatArmor;
    }

    public double getExtraFlatArmorToughness() {
        return extraFlatArmorToughness;
    }

}
