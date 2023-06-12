package me.msicraft.modpackintegrated.CraftingEquip.Task;

import me.msicraft.modpackintegrated.CraftingEquip.Data.PlayerSpecialAbility;
import me.msicraft.modpackintegrated.CraftingEquip.Util.CraftingEquipStatUtil;
import me.msicraft.modpackintegrated.ModPackIntegrated;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class CraftingEquipStatTask extends BukkitRunnable {

    private Player player;

    private static final Map<UUID, Double> beforeMaxHealthMap = new HashMap<>();

    public static void removeMap(Player player) { beforeMaxHealthMap.remove(player.getUniqueId()); }

    public CraftingEquipStatTask(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        if (player.isOnline()) {
            double currentAttackSpeed = CraftingEquipStatUtil.getAttackSpeedStat(player);
            double afterAttackSpeed = CraftingEquipStatUtil.getTotalAttackSpeedStat(player);
            double currentMaxHealth = CraftingEquipStatUtil.getHealthStat(player);
            double afterMaxHealth = CraftingEquipStatUtil.getTotalHealthStat(player);
            CraftingEquipStatUtil.applyEquipmentStatToMap(player);
            if (Double.compare(currentAttackSpeed, afterAttackSpeed) != 0) {
                AttributeInstance instance = player.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
                if (instance != null) {
                    AttributeModifier attackSpeedModifier = new AttributeModifier(UUID.fromString("45f9581f-b5a0-45d1-8b8d-d4fd8ebf4662"), "MPI-CE-AttackSpeedModifier", afterAttackSpeed, AttributeModifier.Operation.ADD_NUMBER);
                    instance.removeModifier(attackSpeedModifier);
                    instance.addModifier(attackSpeedModifier);
                }
            }
            if (Double.compare(currentMaxHealth, afterMaxHealth) != 0) {
                AttributeInstance instance = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                if (instance != null) {
                    int last = (int) CraftingEquipStatUtil.getHealthStat(player);
                    AttributeModifier m = new AttributeModifier(UUID.fromString("54a659f6-4ec9-4f3c-803e-993023a7cba3"), "MPI-CE-HealthModifier", last, AttributeModifier.Operation.ADD_NUMBER);
                    instance.removeModifier(m);
                    instance.addModifier(m);
                }
            }
            Bukkit.getScheduler().runTask(ModPackIntegrated.getPlugin(), ()-> {
                PlayerSpecialAbility playerSpecialAbility = new PlayerSpecialAbility(player);
                removeAbilityModifier(player);
                for (Attribute attribute : modifierAttributes) {
                    int percentM = 0;
                    double flatM = 0;
                    AttributeInstance instance = player.getAttribute(attribute);
                    if (instance != null) {
                        switch (attribute) {
                            case GENERIC_ATTACK_SPEED -> {
                                percentM = playerSpecialAbility.getExtraPercentAttackSpeed();
                                flatM = playerSpecialAbility.getExtraFlatAttackSpeed();
                            }
                            case GENERIC_MOVEMENT_SPEED -> {
                                percentM = playerSpecialAbility.getExtraPercentMovementSpeed();
                                flatM = playerSpecialAbility.getExtraFlatMovementSpeed();
                            }
                            case GENERIC_MAX_HEALTH -> {
                                percentM = playerSpecialAbility.getExtraPercentHealth();
                                flatM = playerSpecialAbility.getExtraFlatHealth();
                            }
                            case GENERIC_ARMOR -> {
                                percentM = playerSpecialAbility.getExtraPercentArmor();
                                flatM = playerSpecialAbility.getExtraFlatArmor();
                            }
                            case GENERIC_ARMOR_TOUGHNESS -> {
                                percentM = playerSpecialAbility.getExtraPercentArmorToughness();
                                flatM = playerSpecialAbility.getExtraFlatArmorToughness();
                            }
                        }
                        if (percentM != 0) {
                            AttributeModifier modifier = getPercentModifier(percentM, instance);
                            instance.addModifier(modifier);
                        }
                        if (Double.compare(flatM, 0) != 0) {
                            AttributeModifier modifier = getFlatModifier(flatM, instance);
                            instance.addModifier(modifier);
                        }
                    }
                }
            });
        } else {
            if (Bukkit.getServer().getScheduler().isCurrentlyRunning(getTaskId())) {
                if (ModPackIntegrated.isDebugEnabled) {
                    Bukkit.getConsoleSender().sendMessage("장비 스탯 스케쥴러 취소: " + player.getName() + " | TaskId: " + getTaskId());
                }
                removeMap(player);
                cancel();
            }
        }
    }

    private static final List<Attribute> modifierAttributes = Arrays.asList(Attribute.GENERIC_ATTACK_SPEED, Attribute.GENERIC_MOVEMENT_SPEED, Attribute.GENERIC_MAX_HEALTH, Attribute.GENERIC_ARMOR, Attribute.GENERIC_ARMOR_TOUGHNESS);

    private void removeAbilityModifier(Player player) {
        for (Attribute attribute : modifierAttributes) {
            AttributeInstance instance = player.getAttribute(attribute);
            if (instance != null) {
                AttributeModifier percentModifier = getPercentModifier(0, instance);
                AttributeModifier flatModifier = getFlatModifier(0, instance);
                instance.removeModifier(percentModifier);
                instance.removeModifier(flatModifier);
            }
        }
    }

    private AttributeModifier getPercentModifier(int amount, AttributeInstance instance) {
        AttributeModifier modifier;
        double value = instance.getValue();
        double cal = value * (amount / 100.0);
        String modifierName = "MPI-CE-ExtraPercent" + instance.getAttribute().name();
        modifier = new AttributeModifier(UUID.fromString("d55c711c-9b67-4122-a289-b075f2e00063"), modifierName, cal, AttributeModifier.Operation.ADD_NUMBER);
        return modifier;
    }

    private AttributeModifier getFlatModifier(double amount, AttributeInstance instance) {
        AttributeModifier modifier;
        String modifierName = "MPI-CE-ExtraFlat" + instance.getAttribute().name();
        modifier = new AttributeModifier(UUID.fromString("987b86f7-6f19-4f06-a7ec-25d8c14a5d57"), modifierName, amount, AttributeModifier.Operation.ADD_NUMBER);
        return modifier;
    }

}
