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

import java.util.*;

public class CraftingEquipStatTask extends BukkitRunnable {

    private Player player;

    private final UUID attackSpeedUUID = UUID.fromString("45f9581f-b5a0-45d1-8b8d-d4fd8ebf4662");
    private static final Map<UUID, Integer> beforeModifierSizeMap = new HashMap<>();

    public CraftingEquipStatTask(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        if (player.isOnline()) {
            double currentAttackSpeed = CraftingEquipStatUtil.getAttackSpeedStat(player);
            double afterAttackSpeed = CraftingEquipStatUtil.getTotalAttackSpeedStat(player);
            CraftingEquipStatUtil.setMeleeStat(player, CraftingEquipStatUtil.getTotalMeleeDamageStat(player));
            CraftingEquipStatUtil.setProjectileStat(player, CraftingEquipStatUtil.getTotalProjectileDamageStat(player));
            CraftingEquipStatUtil.setAttackSpeedStat(player, afterAttackSpeed);
            CraftingEquipStatUtil.setDefenseStat(player, CraftingEquipStatUtil.getTotalDefenseStat(player));
            if (currentAttackSpeed != afterAttackSpeed) {
                AttributeInstance instance = player.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
                if (instance != null) {
                    AttributeModifier attackSpeedModifier = new AttributeModifier(attackSpeedUUID, "MPI-CE-AttackSpeedModifier", afterAttackSpeed, AttributeModifier.Operation.ADD_NUMBER);
                    instance.removeModifier(attackSpeedModifier);
                    instance.addModifier(attackSpeedModifier);
                }
            }
            int modifierSize = 0;
            if (beforeModifierSizeMap.containsKey(player.getUniqueId())) {
                modifierSize = beforeModifierSizeMap.get(player.getUniqueId());
            }
            List<SpecialAbility> modifierAbilities = CraftingEquipStatUtil.getModifierAbility(player);
            int afterModifierSize = modifierAbilities.size();
            if (modifierSize != afterModifierSize) {
                removeAbilityModifier(player);
                Bukkit.getScheduler().runTask(ModPackIntegrated.getPlugin(), ()-> {
                    AttributeInstance instance = null;
                    AttributeModifier modifier = null;
                    for (SpecialAbility specialAbility : modifierAbilities) {
                        switch (specialAbility) {
                            case extraAttackSpeed_5 -> {
                                instance = player.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
                                modifier = getAttackSpeedModifier_5(player);
                            }
                            case extraAttackSpeed_10 -> {
                                instance = player.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
                                modifier = getAttackSpeedModifier_10(player);
                            }
                            case extraAttackSpeed_15 -> {
                                instance = player.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
                                modifier = getAttackSpeedModifier_15(player);
                            }
                            case extraMovementSpeed_5 -> {
                                instance = player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
                                modifier = getMovementSpeedModifier_5(player);
                            }
                            case extraMovementSpeed_10 -> {
                                instance = player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
                                modifier = getMovementSpeedModifier_10(player);
                            }
                        }
                        if (instance != null && modifier != null) {
                            instance.addModifier(modifier);
                        }
                    }
                    beforeModifierSizeMap.put(player.getUniqueId(), modifierAbilities.size());
                });
            }
        } else {
            if (Bukkit.getServer().getScheduler().isCurrentlyRunning(getTaskId())) {
                if (ModPackIntegrated.isDebugEnabled) {
                    Bukkit.getConsoleSender().sendMessage("장비 스탯 스케쥴러 취소: " + player.getName() + " | TaskId: " + getTaskId());
                }
                beforeModifierSizeMap.remove(player.getUniqueId());
                cancel();
            }
        }
    }

    private void removeAbilityModifier(Player player) {
        List<AttributeModifier> attackSpeedModifiers = new ArrayList<>();
        attackSpeedModifiers.add(getAttackSpeedModifier_5(player));
        attackSpeedModifiers.add(getAttackSpeedModifier_10(player));
        attackSpeedModifiers.add(getAttackSpeedModifier_15(player));
        AttributeInstance attackSpeedInstance = player.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
        if (attackSpeedInstance != null) {
            for (AttributeModifier modifier : attackSpeedModifiers) {
                attackSpeedInstance.removeModifier(modifier);
            }
        }
        List<AttributeModifier> movementSpeedModifiers = new ArrayList<>();
        movementSpeedModifiers.add(getMovementSpeedModifier_5(player));
        movementSpeedModifiers.add(getMovementSpeedModifier_10(player));
        AttributeInstance movementSpeedInstance = player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
        if (movementSpeedInstance != null) {
            for (AttributeModifier modifier : movementSpeedModifiers) {
                movementSpeedInstance.removeModifier(modifier);
            }
        }
    }

    private AttributeModifier getAttackSpeedModifier_5(Player player) {
        AttributeModifier modifier = null;
        AttributeInstance instance = player.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
        if (instance != null) {
            double value = instance.getValue();
            double cal = value * 0.05;
            modifier = new AttributeModifier(UUID.fromString("c86f3327-9967-41e2-aaaa-8207d850a31b"), "MPI-CE-ExtraAttackSpeedModifier", cal, AttributeModifier.Operation.ADD_NUMBER);
        }
        return modifier;
    }

    private AttributeModifier getAttackSpeedModifier_10(Player player) {
        AttributeModifier modifier = null;
        AttributeInstance instance = player.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
        if (instance != null) {
            double value = instance.getValue();
            double cal = value * 0.1;
            modifier = new AttributeModifier(UUID.fromString("c0c384c4-0705-425e-8d70-c436bc6a28ca"), "MPI-CE-ExtraAttackSpeedModifier", cal, AttributeModifier.Operation.ADD_NUMBER);
        }
        return modifier;
    }

    private AttributeModifier getAttackSpeedModifier_15(Player player) {
        AttributeModifier modifier = null;
        AttributeInstance instance = player.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
        if (instance != null) {
            double value = instance.getValue();
            double cal = value * 0.15;
            modifier = new AttributeModifier(UUID.fromString("6ab19b2c-237f-4534-a5ce-a845c3b00f8b"), "MPI-CE-ExtraAttackSpeedModifier", cal, AttributeModifier.Operation.ADD_NUMBER);
        }
        return modifier;
    }

    private AttributeModifier getMovementSpeedModifier_5(Player player) {
        AttributeModifier modifier = null;
        AttributeInstance instance = player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
        if (instance != null) {
            double value = instance.getValue();
            double cal = value * 0.05;
            modifier = new AttributeModifier(UUID.fromString("6bb865cb-7e1f-48fd-86d4-494395e10dbe"), "MPI-CE-ExtraAttackSpeedModifier", cal, AttributeModifier.Operation.ADD_NUMBER);
        }
        return modifier;
    }

    private AttributeModifier getMovementSpeedModifier_10(Player player) {
        AttributeModifier modifier = null;
        AttributeInstance instance = player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
        if (instance != null) {
            double value = instance.getValue();
            double cal = value * 0.1;
            modifier = new AttributeModifier(UUID.fromString("a48158fa-ced9-472a-a26d-892bf0472423"), "MPI-CE-ExtraAttackSpeedModifier", cal, AttributeModifier.Operation.ADD_NUMBER);
        }
        return modifier;
    }

}
