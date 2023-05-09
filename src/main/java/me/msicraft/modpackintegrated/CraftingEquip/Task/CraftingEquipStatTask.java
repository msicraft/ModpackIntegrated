package me.msicraft.modpackintegrated.CraftingEquip.Task;

import me.msicraft.modpackintegrated.CraftingEquip.Enum.SpecialAbility;
import me.msicraft.modpackintegrated.CraftingEquip.Util.CraftingEquipStatUtil;
import me.msicraft.modpackintegrated.ModPackIntegrated;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CraftingEquipStatTask extends BukkitRunnable {

    private Player player;

    private final UUID attackSpeedUUID = UUID.fromString("45f9581f-b5a0-45d1-8b8d-d4fd8ebf4662");

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
                    AttributeModifier attackSpeedModifier = new AttributeModifier(attackSpeedUUID, "MPI-CE-AttackSpeedModifier", afterAttackSpeed, AttributeModifier.Operation.ADD_NUMBER);
                    instance.removeModifier(attackSpeedModifier);
                    instance.addModifier(attackSpeedModifier);
                }
            }
            if (Double.compare(currentMaxHealth, afterMaxHealth) != 0) {
                AttributeInstance instance = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                if (instance != null) {
                    int last = (int) afterMaxHealth;
                    AttributeModifier m = getFlatMaxHealthModifier(player, last);
                    instance.removeModifier(m);
                    instance.addModifier(m);
                }
            }
            Bukkit.getScheduler().runTask(ModPackIntegrated.getPlugin(), ()-> {
                List<SpecialAbility> modifierAbilities = CraftingEquipStatUtil.getModifierAbility(player);
                removeAbilityModifier(player);
                int attackSpeedM = 0;
                int movementSpeedM = 0;
                for (SpecialAbility specialAbility : modifierAbilities) {
                    switch (specialAbility) {
                        case extraAttackSpeed_5 -> attackSpeedM = attackSpeedM + 5;
                        case extraAttackSpeed_10 -> attackSpeedM = attackSpeedM + 10;
                        case extraAttackSpeed_15 -> attackSpeedM = attackSpeedM + 15;
                        case extraMovementSpeed_5 -> movementSpeedM = movementSpeedM + 5;
                        case extraMovementSpeed_10 -> movementSpeedM = movementSpeedM + 10;
                    }
                }
                if (attackSpeedM != 0) {
                    AttributeInstance instance = player.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
                    if (instance != null) {
                        AttributeModifier modifier = getMultipleAttackSpeedModifier(player, attackSpeedM);
                        if (modifier != null) {
                            instance.addModifier(modifier);
                        }
                    }
                }
                if (movementSpeedM != 0) {
                    AttributeInstance instance = player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
                    if (instance != null) {
                        AttributeModifier modifier = getMultipleMovementSpeedModifier(player, movementSpeedM);
                        if (modifier != null) {
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

    private void removeAbilityModifier(Player player) {
        AttributeInstance attackSpeedInstance = player.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
        if (attackSpeedInstance != null) {
            AttributeModifier modifier = getMultipleAttackSpeedModifier(player, 0);
            attackSpeedInstance.removeModifier(modifier);
        }
        AttributeInstance movementSpeedInstance = player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
        if (movementSpeedInstance != null) {
            AttributeModifier modifier = getMultipleMovementSpeedModifier(player, 0);
            movementSpeedInstance.removeModifier(modifier);
        }
    }

    private void removeHealthModifier(Player player) {
        AttributeInstance maxHealthInstance = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (maxHealthInstance != null) {
            AttributeModifier modifier = getFlatMaxHealthModifier(player, 0);
            maxHealthInstance.removeModifier(modifier);
        }
    }

    private AttributeModifier getMultipleAttackSpeedModifier(Player player, int amount) {
        AttributeModifier modifier = null;
        AttributeInstance instance = player.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
        if (instance != null) {
            double value = instance.getValue();
            double cal = value * (amount / 100.0);
            modifier = new AttributeModifier(UUID.fromString("c86f3327-9967-41e2-aaaa-8207d850a31b"), "MPI-CE-ExtraAttackSpeedModifier", cal, AttributeModifier.Operation.ADD_NUMBER);
        }
        return modifier;
    }

    private AttributeModifier getMultipleMovementSpeedModifier(Player player, int amount) {
        AttributeModifier modifier = null;
        AttributeInstance instance = player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
        if (instance != null) {
            double value = instance.getValue();
            double cal = value * (amount / 100.0);
            modifier = new AttributeModifier(UUID.fromString("6bb865cb-7e1f-48fd-86d4-494395e10dbe"), "MPI-CE-ExtraAttackSpeedModifier", cal, AttributeModifier.Operation.ADD_NUMBER);
        }
        return modifier;
    }

    private AttributeModifier getFlatMaxHealthModifier(Player player, double amount) {
        AttributeModifier modifier = null;
        AttributeInstance instance = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (instance != null) {
            modifier = new AttributeModifier(UUID.fromString("54a659f6-4ec9-4f3c-803e-993023a7cba3"), "MPI-CE-ExtraMaxHealthModifier", amount, AttributeModifier.Operation.ADD_NUMBER);
        }
        return modifier;
    }

}
