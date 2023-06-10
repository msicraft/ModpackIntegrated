package me.msicraft.modpackintegrated.CraftingEquip.Util;

import me.msicraft.modpackintegrated.CraftingEquip.Enum.EquipmentType;
import me.msicraft.modpackintegrated.CraftingEquip.Enum.SpecialAbility;
import me.msicraft.modpackintegrated.CraftingEquip.Task.CraftingEquipStatTask;
import me.msicraft.modpackintegrated.Event.PlayerRelated;
import me.msicraft.modpackintegrated.ModPackIntegrated;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class CraftingEquipStatUtil {

    private static final Map<UUID, Map<String, Double>> equipStatMap = new HashMap<>(8);

    private static boolean isEnabledCheckEquipmentType = false;

    public static void reloadVariables() {
        isEnabledCheckEquipmentType = ModPackIntegrated.getPlugin().getConfig().contains("CraftingEquipment.EquipmentTypeCheck") && ModPackIntegrated.getPlugin().getConfig().getBoolean("CraftingEquipment.EquipmentTypeCheck");
    }

    public void registerStatMapTask(Player player) {
        BukkitTask statTask = new CraftingEquipStatTask(player).runTaskTimer(ModPackIntegrated.getPlugin(), 20L, 2L);
        PlayerRelated.addActiveTasks(player, statTask.getTaskId());
        if (ModPackIntegrated.isDebugEnabled) {
            Bukkit.getConsoleSender().sendMessage("장비 스탯 스케쥴러 등록: " + player.getName() + " | TaskId: " + statTask.getTaskId());
        }
    }

    public void registerStatMap(Player player) {
        Map<String, Double> map = new HashMap<>();
        map.put("MeleeDamage", 0.0);
        map.put("ProjectileDamage", 0.0);
        map.put("AttackSpeed", 0.0);
        map.put("Defense", 0.0);
        map.put("Health", 0.0);
        map.put("Critical", 0.0);
        equipStatMap.put(player.getUniqueId(), map);
    }

    public static boolean containStatMap(Player player) {
        return equipStatMap.containsKey(player.getUniqueId());
    }

    public static Map<String, Double> getStatMap(Player player) { return equipStatMap.get(player.getUniqueId()); }

    public void removeStatMap(Player player) {
        equipStatMap.remove(player.getUniqueId());
    }

    public static double getMeleeValue(Player player) { return getStatMap(player).get("MeleeDamage"); }
    public static double getProjectileValue(Player player) { return getStatMap(player).get("ProjectileDamage"); }
    public static double getDefenseValue(Player player) { return getStatMap(player).get("Defense"); }

    public static double getHealthStat(Player player) { return getStatMap(player).get("Health"); }
    public static double getAttackSpeedStat(Player player) { return getStatMap(player).get("AttackSpeed"); }

    public static double getCriticalStat(Player player) { return getStatMap(player).get("Critical"); }

    public static EquipmentType getEquipmentType(ItemStack itemStack) {
        EquipmentType type = null;
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            PersistentDataContainer data = itemMeta.getPersistentDataContainer();
            if (data.has(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI-CE-EquipmentType"), PersistentDataType.STRING)) {
                String s = data.get(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI-CE-EquipmentType"), PersistentDataType.STRING);
                if (s != null) {
                    try {
                        type = EquipmentType.valueOf(s);
                    } catch (IllegalArgumentException ignored) {}
                }
            }
        }
        return type;
    }

    private static final List<EquipmentSlot> armorTypeSlots = Arrays.asList(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET);
    private static final EquipmentSlot weaponSlot = EquipmentSlot.HAND;
    private static final EquipmentSlot catalystSlot = EquipmentSlot.OFF_HAND;

    public static void setMeleeStat(Player player, double amount) { getStatMap(player).put("MeleeDamage", amount); }
    public static void setProjectileStat(Player player, double amount) { getStatMap(player).put("ProjectileDamage", amount); }
    public static void setAttackSpeedStat(Player player, double amount) { getStatMap(player).put("AttackSpeed", amount); }
    public static void setDefenseStat(Player player, double amount) { getStatMap(player).put("Defense", amount); }
    public static void setHealthStat(Player player, double amount) { getStatMap(player).put("Health", amount); }
    public static void setCriticalStat(Player player, double amount) { getStatMap(player).put("Critical", amount); }

    public static boolean checkEqualEquipmentTypeToSlot(ItemStack itemStack, EquipmentSlot slot) {
        boolean check = false;
        if (isEnabledCheckEquipmentType) {
            EquipmentType equipmentType = getEquipmentType(itemStack);
            if (equipmentType != null) {
                switch (equipmentType) {
                    case weapon -> {
                        if (slot == weaponSlot) {
                            check = true;
                        }
                    }
                    case armor -> {
                        if (armorTypeSlots.contains(slot)) {
                            check = true;
                        }
                    }
                    case catalyst -> {
                        if (slot == catalystSlot) {
                            check = true;
                        }
                    }
                }
            }
        } else {
            check = true;
        }
        return check;
    }

    public static void applyEquipmentStatToMap(Player player) {
        double melee = 0;
        double projectile = 0;
        double attackSpeed = 0;
        double defense = 0;
        double health = 0;
        double critical = 0;
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack itemStack = player.getInventory().getItem(slot);
            if (itemStack != null && itemStack.getType() != Material.AIR) {
                if (checkEqualEquipmentTypeToSlot(itemStack, slot)) {
                    melee = melee + getMeleeDamage(itemStack);
                    projectile = projectile + getProjectileDamage(itemStack);
                    attackSpeed = attackSpeed + getAttackSpeed(itemStack);
                    defense = defense + getDefense(itemStack);
                    health = health + getHealth(itemStack);
                    critical = critical + getCritical(itemStack);
                }
            }
        }
        setMeleeStat(player, melee);
        setProjectileStat(player, projectile);
        setAttackSpeedStat(player, attackSpeed);
        setDefenseStat(player, defense);
        setHealthStat(player, health);
        setCriticalStat(player, critical);
    }

    public static double getTotalMeleeDamageStat(Player player) {
        double value = 0;
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack itemStack = player.getInventory().getItem(slot);
            if (itemStack != null && itemStack.getType() != Material.AIR) {
                value = value + getMeleeDamage(itemStack);
            }
        }
        return value;
    }

    public static double getTotalProjectileDamageStat(Player player) {
        double value = 0;
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack itemStack = player.getInventory().getItem(slot);
            if (itemStack != null && itemStack.getType() != Material.AIR) {
                value = value + getProjectileDamage(itemStack);
            }
        }
        return value;
    }

    public static double getTotalAttackSpeedStat(Player player) {
        double value = 0;
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack itemStack = player.getInventory().getItem(slot);
            if (itemStack != null && itemStack.getType() != Material.AIR) {
                value = value + getAttackSpeed(itemStack);
            }
        }
        return value;
    }

    public static double getTotalDefenseStat(Player player) {
        double value = 0;
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack itemStack = player.getInventory().getItem(slot);
            if (itemStack != null && itemStack.getType() != Material.AIR) {
                value = value + getDefense(itemStack);
            }
        }
        return value;
    }

    public static double getTotalHealthStat(Player player) {
        double value = 0;
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack itemStack = player.getInventory().getItem(slot);
            if (itemStack != null && itemStack.getType() != Material.AIR) {
                value = value + getHealth(itemStack);
            }
        }
        return value;
    }

    public static double getMeleeDamage(ItemStack itemStack) {
        double value = 0;
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            PersistentDataContainer data = itemMeta.getPersistentDataContainer();
            if (data.has(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI-CE-Melee"), PersistentDataType.STRING)) {
                String s = data.get(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI-CE-Melee"), PersistentDataType.STRING);
                if (s != null) {
                    value = Double.parseDouble(s);
                }
            }
        }
        return value;
    }

    public static double getProjectileDamage(ItemStack itemStack) {
        double value = 0;
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            PersistentDataContainer data = itemMeta.getPersistentDataContainer();
            if (data.has(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI-CE-Projectile"), PersistentDataType.STRING)) {
                String s = data.get(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI-CE-Projectile"), PersistentDataType.STRING);
                if (s != null) {
                    value = Double.parseDouble(s);
                }
            }
        }
        return value;
    }

    public static double getAttackSpeed(ItemStack itemStack) {
        double value = 0;
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            PersistentDataContainer data = itemMeta.getPersistentDataContainer();
            if (data.has(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI-CE-AttackSpeed"), PersistentDataType.STRING)) {
                String s = data.get(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI-CE-AttackSpeed"), PersistentDataType.STRING);
                if (s != null) {
                    value = Double.parseDouble(s);
                }
            }
        }
        return value;
    }

    public static double getDefense(ItemStack itemStack) {
        double value = 0;
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            PersistentDataContainer data = itemMeta.getPersistentDataContainer();
            if (data.has(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI-CE-Defense"), PersistentDataType.STRING)) {
                String s = data.get(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI-CE-Defense"), PersistentDataType.STRING);
                if (s != null) {
                    value = Double.parseDouble(s);
                }
            }
        }
        return value;
    }

    public static double getHealth(ItemStack itemStack) {
        double value = 0;
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            PersistentDataContainer data = itemMeta.getPersistentDataContainer();
            if (data.has(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI-CE-Health"), PersistentDataType.STRING)) {
                String s = data.get(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI-CE-Health"), PersistentDataType.STRING);
                if (s != null) {
                    value = Double.parseDouble(s);
                }
            }
        }
        return value;
    }

    public static double getCritical(ItemStack itemStack) {
        double v = 0;
        SpecialAbility specialAbility = getSpecialAbility(itemStack);
        if (specialAbility == SpecialAbility.doubleDamage) {
            v = SpecialAbilityInfo.getPercent(itemStack);
        }
        return v;
    }

    public static boolean hasItemSpecialAbility(ItemStack itemStack) {
        boolean check = false;
        if (itemStack != null && itemStack.getType() != Material.AIR) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta != null) {
                PersistentDataContainer data = itemMeta.getPersistentDataContainer();
                if (data.has(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI-CE-SpecialAbility"), PersistentDataType.STRING)) {
                    check = true;
                }
            }
        }
        return check;
    }

    public static boolean hasSpecialAbilityEquipment(Player player) {
        boolean check = false;
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack itemStack = player.getInventory().getItem(slot);
            if (itemStack != null && itemStack.getType() != Material.AIR) {
                ItemMeta itemMeta = itemStack.getItemMeta();
                if (itemMeta != null) {
                    PersistentDataContainer data = itemMeta.getPersistentDataContainer();
                    if (data.has(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI-CE-SpecialAbility"), PersistentDataType.STRING)) {
                        check = true;
                    }
                }
            }
        }
        return check;
    }

    public static void applySpecialAbility(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            SpecialAbility specialAbility = CraftingEquipUtil.getRandomAbility();
            SpecialAbilityInfo.applyItemStackToSpecialAbility(itemStack, specialAbility);
            updateAbilityLore(itemStack);
        }
    }

    public static void rerollSpecialAbility(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            SpecialAbility specialAbility = CraftingEquipUtil.getRandomAbility();
            SpecialAbilityInfo.applyItemStackToSpecialAbility(itemStack, specialAbility);
            updateAbilityLore(itemStack);
        }
    }

    public static void setSpecialAbility(SpecialAbility specialAbility, ItemStack itemStack) {
        SpecialAbilityInfo.applyItemStackToSpecialAbility(itemStack, specialAbility);
        updateAbilityLore(itemStack);
    }

    public static SpecialAbility getSpecialAbility(ItemStack itemStack) {
        SpecialAbility specialAbility = null;
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            PersistentDataContainer data = itemMeta.getPersistentDataContainer();
            if (data.has(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI-CE-SpecialAbility"), PersistentDataType.STRING)) {
                String s = data.get(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI-CE-SpecialAbility"), PersistentDataType.STRING);
                if (s != null) {
                    try {
                        specialAbility = SpecialAbility.valueOf(s);
                    } catch (IllegalArgumentException ignored) {}
                }
            }
        }
        return specialAbility;
    }

    public static List<SpecialAbility> getContainSpecialAbilities(Player player) {
        List<SpecialAbility> list = new ArrayList<>();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack itemStack = player.getInventory().getItem(slot);
            if (itemStack != null && itemStack.getType() != Material.AIR) {
                if (checkEqualEquipmentTypeToSlot(itemStack, slot)) {
                    SpecialAbility specialAbility = getSpecialAbility(itemStack);
                    if (specialAbility != null) {
                        if (!list.contains(specialAbility)) {
                            list.add(specialAbility);
                        }
                    }
                }
            }
        }
        return list;
    }

    public static void updateAbilityLore(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            List<String> lore = itemMeta.getLore();
            if (lore != null && !lore.isEmpty()) {
                SpecialAbility originalAbility = getSpecialAbility(itemStack);
                String info;
                if (originalAbility != null) {
                    info = SpecialAbilityInfo.getLoreAboutItemStack(itemStack, originalAbility);
                } else {
                    info = ChatColor.RED + "X";
                    PersistentDataContainer data = itemMeta.getPersistentDataContainer();
                    data.remove(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI-CE-SpecialAbility"));
                    data.set(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI-CE-SpecialAbility"), PersistentDataType.STRING, "none");
                }
                List<String> replaceLore = new ArrayList<>();
                for (String s : lore) {
                    if (s.contains("특수 능력:")) {
                        replaceLore.add(ChatColor.GRAY + "특수 능력: " + info);
                    } else {
                        replaceLore.add(s);
                    }
                }
                itemMeta.setLore(replaceLore);
                itemStack.setItemMeta(itemMeta);
            }
        }
    }

}
