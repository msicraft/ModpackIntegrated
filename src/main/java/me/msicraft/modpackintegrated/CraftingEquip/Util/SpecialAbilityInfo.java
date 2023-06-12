package me.msicraft.modpackintegrated.CraftingEquip.Util;

import me.msicraft.modpackintegrated.CraftingEquip.Enum.SpecialAbility;
import me.msicraft.modpackintegrated.ModPackIntegrated;
import me.msicraft.modpackintegrated.Util.MathUtil;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.List;

public class SpecialAbilityInfo {

    private static final List<SpecialAbility> percentSpecialAbilities = Arrays.asList(SpecialAbility.doubleDamage, SpecialAbility.lifeDrain);

    private static final List<SpecialAbility> multiValueSpecialAbilities = Arrays.asList(SpecialAbility.increaseTakeDamageAndExtraDamage,
            SpecialAbility.increaseMaxHealthAndDecreaseDamage,SpecialAbility.decreaseTakeAndAttackDamage);

    /*
    public static final List<SpecialAbility> modifierSpecialAbilities = Arrays.asList(SpecialAbility.extraAttackSpeed, SpecialAbility.extraMovementSpeed,
            SpecialAbility.increaseMaxHealth, SpecialAbility.increaseMaxHealthAndDecreaseDamage);
     */

    public static final List<SpecialAbility> highPriorityAbilities = Arrays.asList(SpecialAbility.lifeDrain, SpecialAbility.damageConvertTrueDamage);

    public static boolean hasPercent(SpecialAbility specialAbility) {
        return percentSpecialAbilities.contains(specialAbility);
    }

    public static boolean hasMultiValue(SpecialAbility specialAbility) {
        return multiValueSpecialAbilities.contains(specialAbility);
    }

    public static double getMaxPercent(SpecialAbility specialAbility) {
        double p = 0;
        switch (specialAbility) {
            case doubleDamage -> p = 15.0;
            case lifeDrain -> p = 15;
        }
        return p;
    }

    public static double getMaxValue(SpecialAbility specialAbility) {
        double v = 0;
        switch (specialAbility) {
            case lifeDrain -> v = 20;
            case extraDamage -> v = 5;
            case extraDamagePlayerBaseHealth -> v = 10;
            case extraAttackSpeed -> v = 10;
            case extraMovementSpeed -> v = 5;
            case extraDamageToDay -> v = 15;
            case extraDamageToNight -> v = 15.0;
            case heal -> v = 3;
            case changeDamageToRange -> v = 25.0;
            case increaseMaxHealth -> v = 8;
            case extraDamageTargetMaxHealth -> v = 4.0;
            case extraDamageTargetCurrentHealth -> v = 10;
            case damageConvertTrueDamage -> v = 8;
            case healBaseMaxHealth -> v = 5.0;
            case takeDamageConvertDotDamage -> v = 20;
            case extraBackAttackDamage -> v = 20;
            case extraArmor -> v = 10;
            case addArmor -> v = 4.5;
            case extraKillPointExp -> v = 15;
        }
        return v;
    }

    public static double getMaxValue_1(SpecialAbility specialAbility) {
        double v = 0;
        switch (specialAbility) {
            case increaseTakeDamageAndExtraDamage -> v = 15;
            case increaseMaxHealthAndDecreaseDamage -> v = 15;
            case decreaseTakeAndAttackDamage -> v = 10;
        }
        return v;
    }

    public static double getMaxValue_2(SpecialAbility specialAbility) {
        double v = 0;
        switch (specialAbility) {
            case increaseTakeDamageAndExtraDamage -> v = 15;
            case increaseMaxHealthAndDecreaseDamage -> v = 20;
            case decreaseTakeAndAttackDamage -> v = 15.0
            ;
        }
        return v;
    }

    public static String getLoreAboutMenuList(SpecialAbility specialAbility) { //메뉴 특수 능력 설명에서 사용
        String a = null;
        if (ModPackIntegrated.specialAbilityInfoFile.getConfig().contains("Ability." + specialAbility.name())) {
            a = ModPackIntegrated.specialAbilityInfoFile.getConfig().getString("Ability." + specialAbility.name());
            if (a != null) {
                if (hasPercent(specialAbility)) {
                    double percent = getMaxPercent(specialAbility);
                    a = a.replace("<percent>", String.valueOf(percent));
                }
                if (hasMultiValue(specialAbility)) {
                    double value1 = getMaxValue_1(specialAbility);
                    double value2 = getMaxValue_2(specialAbility);
                    a = a.replace("<value-1>", String.valueOf(value1));
                    a = a.replace("<value-2>", String.valueOf(value2));
                } else {
                    double value = getMaxValue(specialAbility);
                    a = a.replace("<value>", String.valueOf(value));
                }
                a = ChatColor.translateAlternateColorCodes('&', a);
            }
        }
        return a;
    }

    public static void applyItemStackToSpecialAbility(ItemStack itemStack, SpecialAbility specialAbility) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            PersistentDataContainer data = itemMeta.getPersistentDataContainer();
            data.set(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI-CE-SpecialAbility"), PersistentDataType.STRING, specialAbility.name());
            if (hasPercent(specialAbility)) {
                double percent = MathUtil.getRandomValueDouble(getMaxPercent(specialAbility), 0);
                data.set(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI-CE-SpecialAbility_Percent"), PersistentDataType.STRING, String.valueOf(percent));
            }
            if (hasMultiValue(specialAbility)) {
                double value1 = MathUtil.getRandomValueDouble(getMaxValue_1(specialAbility), 0);
                double value2 = MathUtil.getRandomValueDouble(getMaxValue_2(specialAbility), 0);
                data.set(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI-CE-SpecialAbility_Value1"), PersistentDataType.STRING, String.valueOf(value1));
                data.set(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI-CE-SpecialAbility_Value2"), PersistentDataType.STRING, String.valueOf(value2));
            } else {
                double value = MathUtil.getRandomValueDouble(getMaxValue(specialAbility), 0);
                data.set(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI-CE-SpecialAbility_Value"), PersistentDataType.STRING, String.valueOf(value));
            }
            itemStack.setItemMeta(itemMeta);
        }
    }

    public static double getPercent(ItemStack itemStack) {
        double v = 0;
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            PersistentDataContainer data = itemMeta.getPersistentDataContainer();
            if (data.has(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI-CE-SpecialAbility_Percent"), PersistentDataType.STRING)) {
                String s = data.get(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI-CE-SpecialAbility_Percent"), PersistentDataType.STRING);
                if (s != null) {
                    v = Double.parseDouble(s);
                }
            }
        }
        return v;
    }

    public static double getValue(ItemStack itemStack) {
        double v = 0;
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            PersistentDataContainer data = itemMeta.getPersistentDataContainer();
            if (data.has(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI-CE-SpecialAbility_Value"), PersistentDataType.STRING)) {
                String s = data.get(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI-CE-SpecialAbility_Value"), PersistentDataType.STRING);
                if (s != null) {
                    v = Double.parseDouble(s);
                }
            }
        }
        return v;
    }

    public static double getValue_1(ItemStack itemStack) {
        double v = 0;
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            PersistentDataContainer data = itemMeta.getPersistentDataContainer();
            if (data.has(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI-CE-SpecialAbility_Value1"), PersistentDataType.STRING)) {
                String s = data.get(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI-CE-SpecialAbility_Value1"), PersistentDataType.STRING);
                if (s != null) {
                    v = Double.parseDouble(s);
                }
            }
        }
        return v;
    }

    public static double getValue_2(ItemStack itemStack) {
        double v = 0;
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            PersistentDataContainer data = itemMeta.getPersistentDataContainer();
            if (data.has(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI-CE-SpecialAbility_Value2"), PersistentDataType.STRING)) {
                String s = data.get(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI-CE-SpecialAbility_Value2"), PersistentDataType.STRING);
                if (s != null) {
                    v = Double.parseDouble(s);
                }
            }
        }
        return v;
    }

    public static String getLoreAboutItemStack(ItemStack itemStack, SpecialAbility specialAbility) {
        String a = null;
        if (ModPackIntegrated.specialAbilityInfoFile.getConfig().contains("Ability." + specialAbility.name())) {
            a = ModPackIntegrated.specialAbilityInfoFile.getConfig().getString("Ability." + specialAbility.name());
            if (a != null) {
                if (hasPercent(specialAbility)) {
                    double percent = getPercent(itemStack);
                    a = a.replace("<percent>", String.valueOf(percent));
                }
                if (hasMultiValue(specialAbility)) {
                    double value1 = getValue_1(itemStack);
                    double value2 = getValue_2(itemStack);
                    a = a.replace("<value-1>", String.valueOf(value1));
                    a = a.replace("<value-2>", String.valueOf(value2));
                } else {
                    double value = getValue(itemStack);
                    a = a.replace("<value>", String.valueOf(value));
                }
                a = ChatColor.translateAlternateColorCodes('&', a);
            }
        }
        return a;
    }

}
