package me.msicraft.modpackintegrated.CraftingEquip.Util;

import me.msicraft.modpackintegrated.CraftingEquip.CraftingInfo;
import me.msicraft.modpackintegrated.CraftingEquip.Enum.EquipmentType;
import me.msicraft.modpackintegrated.CraftingEquip.Enum.SpecialAbility;
import me.msicraft.modpackintegrated.ModPackIntegrated;
import me.msicraft.modpackintegrated.Util.PlayerUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CraftingEquipUtil {

    public static boolean isCraftingEquipment(ItemStack itemStack) {
        boolean check = false;
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            PersistentDataContainer data = itemMeta.getPersistentDataContainer();
            if (data.has(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI-CraftingEquipment"), PersistentDataType.STRING)) {
                check = true;
            }
        }
        return check;
    }

    public static void applyCraftingEquipmentTag(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            PersistentDataContainer data = itemMeta.getPersistentDataContainer();
            data.set(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI-CraftingEquipment"), PersistentDataType.STRING, "CraftingEquipment");
            itemStack.setItemMeta(itemMeta);
        }
    }

    public static int requiredSpecialAbility() {
        int value = 0;
        if (ModPackIntegrated.getPlugin().getConfig().contains("CraftingEquipment.SpecialAbility.KillPoint")) {
            value = ModPackIntegrated.getPlugin().getConfig().getInt("CraftingEquipment.SpecialAbility.KillPoint");
        }
        return value;
    }

    public static int requiredMeleeDamage() {
        int value = 0;
        if (ModPackIntegrated.getPlugin().getConfig().contains("CraftingEquipment.MeleeDamage.KillPoint")) {
            value = ModPackIntegrated.getPlugin().getConfig().getInt("CraftingEquipment.MeleeDamage.KillPoint");
        }
        return value;
    }

    public static int requiredProjectileDamage() {
        int value = 0;
        if (ModPackIntegrated.getPlugin().getConfig().contains("CraftingEquipment.ProjectileDamage.KillPoint")) {
            value = ModPackIntegrated.getPlugin().getConfig().getInt("CraftingEquipment.ProjectileDamage.KillPoint");
        }
        return value;
    }

    public static int requiredDefense() {
        int value = 0;
        if (ModPackIntegrated.getPlugin().getConfig().contains("CraftingEquipment.Defense.KillPoint")) {
            value = ModPackIntegrated.getPlugin().getConfig().getInt("CraftingEquipment.Defense.KillPoint");
        }
        return value;
    }

    public static int requiredAttackSpeed() {
        int value = 0;
        if (ModPackIntegrated.getPlugin().getConfig().contains("CraftingEquipment.AttackSpeed.KillPoint")) {
            value = ModPackIntegrated.getPlugin().getConfig().getInt("CraftingEquipment.AttackSpeed.KillPoint");
        }
        return value;
    }

    public static void sendPlayerToCraftingInfo(Player player, ItemStack itemStack, int playerSlot) {
        if (isCraftingEquipment(itemStack)) {
            player.sendMessage(ChatColor.RED + "이미 제작된 장비 입니다.");
        } else {
            CraftingInfo craftingInfo = new CraftingInfo(player);
            ItemStack getBaseItemStack = craftingInfo.getBaseItemStack();
            if (getBaseItemStack.getType() == Material.AIR) {
                craftingInfo.setBaseItemStack(itemStack);
                player.getInventory().setItem(playerSlot, craftingInfo.getAirStack());
                craftingInfo.saveInfo();
            } else {
                player.sendMessage(ChatColor.RED + "이미 재료 장비가 존재합니다.");
            }
        }
    }

    public static void sendCraftingInfoToPlayer(Player player) {
        CraftingInfo craftingInfo = new CraftingInfo(player);
        ItemStack itemStack = craftingInfo.getBaseItemStack();
        if (itemStack.getType() != Material.AIR) {
            craftingInfo.setBaseItemStack(craftingInfo.getAirStack());
            int slot = PlayerUtil.getPlayerEmptySlot(player);
            if (slot != -1) {
                player.getInventory().addItem(itemStack);
            } else {
                player.getWorld().dropItemNaturally(player.getLocation(), itemStack);
            }
            craftingInfo.saveInfo();
        }
    }

    private static final Random random = new Random();

    public static double getRandomValue(double max, double min) {
        double randomValue = (Math.random() * (max - min)) + min;
        return (Math.floor(randomValue * 100) / 100.0);
    }

    public static SpecialAbility getRandomAbility() {
        int max = SpecialAbility.values().length;
        int rn = random.nextInt(max);
        return SpecialAbility.values()[rn];
    }

    public static ItemStack getExpectedItemStack(Player player) {
        CraftingInfo craftingInfo = new CraftingInfo(player);
        ItemStack itemStack = craftingInfo.getBaseItemStack();
        if (itemStack.getType() != Material.AIR) {
            List<String> lore = new ArrayList<>();
            ItemStack tempStack = new ItemStack(itemStack);
            ItemMeta itemMeta = tempStack.getItemMeta();
            if (itemMeta != null) {
                itemMeta.setDisplayName(ChatColor.GREEN + "예상 제작 장비");
                EquipmentType equipmentType = craftingInfo.getEquipmentType();
                boolean isApplySpecialAbility = craftingInfo.isApplySpecialAbility();
                int getMelee = craftingInfo.getAddMeleeDamage();
                int getProjectile = craftingInfo.getAddProjectileDamage();
                int getAttackSpeed = craftingInfo.getAddAttackSpeed();
                int getDefense = craftingInfo.getAddDefense();
                double maxMeleeValue = 0,minMeleeValue = 0;
                double maxProjectileValue = 0,minProjectileValue = 0;
                double maxAttackSpeedValue = 0,minAttackSpeedValue = 0;
                double maxDefenseValue = 0,minDefenseValue = 0;
                for (int a = 0; a<getMelee; a++) {
                    maxMeleeValue += getMeleeMaxAddRange();
                    minMeleeValue += getMeleeMinAddRange();
                    minAttackSpeedValue -= getAttackSpeedMaxRemoveRange();
                    maxAttackSpeedValue -= getAttackSpeedMinRemoveRange();
                }
                for (int a = 0; a<getProjectile; a++) {
                    maxProjectileValue += getProjectileMaxAddRange();
                    minProjectileValue += getProjectileMinAddRange();
                    minDefenseValue -= getDefenseMaxRemoveRange();
                    maxDefenseValue -= getDefenseMinRemoveRange();
                }
                for (int a = 0; a<getAttackSpeed; a++) {
                    maxAttackSpeedValue += getAttackSpeedMaxAddRange();
                    minAttackSpeedValue += getAttackSpeedMinAddRange();
                    minMeleeValue -= getMeleeMaxRemoveRange();
                    maxMeleeValue -= getMeleeMinRemoveRange();
                }
                for (int a = 0; a<getDefense; a++) {
                    maxDefenseValue += getDefenseMaxAddRange();
                    minDefenseValue += getDefenseMinAddRange();
                    int randomV = random.nextInt(2);
                    if (randomV == 0) {
                        minMeleeValue -= getMeleeMaxRemoveRange();
                        maxMeleeValue -= getMeleeMinRemoveRange();
                    } else {
                        minProjectileValue -= getProjectileMaxRemoveRange();
                        maxProjectileValue -= getProjectileMinRemoveRange();
                    }
                }
                if (isApplySpecialAbility) {
                    if (equipmentType == EquipmentType.weapon || equipmentType == EquipmentType.armor) {
                        lore.add(ChatColor.GREEN + "50%의 확률로 특수 능력 " + ChatColor.GRAY + SpecialAbility.values().length + ChatColor.GREEN + " 개 중 " + ChatColor.GRAY + "1" + ChatColor.GREEN +" 개 부여");
                    }
                } else {
                    lore.add(ChatColor.GREEN + "특수 능력 부여되지 않음");
                }
                String melee1, melee2;
                String projectile1, projectile2;
                String attackSpeed1, attackSpeed2;
                String defense1, defense2;
                if (minMeleeValue > 0) {
                    melee1 = ChatColor.GREEN + "" + minMeleeValue;
                } else if (minMeleeValue == 0) {
                    melee1 = ChatColor.WHITE + "" + minMeleeValue;
                } else {
                    melee1 = ChatColor.RED + "" + minMeleeValue;
                }
                if (maxMeleeValue > 0) {
                    melee2 = ChatColor.GREEN + "" + maxMeleeValue;
                } else if (maxMeleeValue == 0) {
                    melee2 = ChatColor.WHITE + "" + maxMeleeValue;
                } else {
                    melee2 = ChatColor.RED + "" + maxMeleeValue;
                }
                if (minProjectileValue > 0) {
                    projectile1 = ChatColor.GREEN + "" + minProjectileValue;
                } else if (minMeleeValue == 0) {
                    projectile1 = ChatColor.WHITE + "" + minProjectileValue;
                } else {
                    projectile1 = ChatColor.RED + "" + minProjectileValue;
                }
                if (maxProjectileValue > 0) {
                    projectile2 = ChatColor.GREEN + "" + maxProjectileValue;
                } else if (maxProjectileValue == 0) {
                    projectile2 = ChatColor.WHITE + "" + maxProjectileValue;
                } else {
                    projectile2 = ChatColor.RED + "" + maxProjectileValue;
                }
                if (minAttackSpeedValue > 0) {
                    attackSpeed1 = ChatColor.GREEN + "" + minAttackSpeedValue;
                } else if (minAttackSpeedValue == 0) {
                    attackSpeed1 = ChatColor.WHITE + "" + minAttackSpeedValue;
                } else {
                    attackSpeed1 = ChatColor.RED + "" + minAttackSpeedValue;
                }
                if (maxAttackSpeedValue > 0) {
                    attackSpeed2 = ChatColor.GREEN + "" + maxAttackSpeedValue;
                } else if (maxAttackSpeedValue == 0) {
                    attackSpeed2 = ChatColor.WHITE + "" + maxAttackSpeedValue;
                } else {
                    attackSpeed2 = ChatColor.RED + "" + maxAttackSpeedValue;
                }
                if (minDefenseValue > 0) {
                    defense1 = ChatColor.GREEN + "" + minDefenseValue;
                } else if (minAttackSpeedValue == 0) {
                    defense1 = ChatColor.WHITE + "" + minDefenseValue;
                } else {
                    defense1 = ChatColor.RED + "" + minDefenseValue;
                }
                if (maxDefenseValue > 0) {
                    defense2 = ChatColor.GREEN + "" + maxDefenseValue;
                } else if (maxAttackSpeedValue == 0) {
                    defense2 = ChatColor.WHITE + "" + maxDefenseValue;
                } else {
                    defense2 = ChatColor.RED + "" + maxDefenseValue;
                }
                lore.add(ChatColor.GREEN + "추가 근접 데미지: " + melee1 + ChatColor.GRAY + " ~ " + melee2);
                lore.add(ChatColor.GREEN + "추가 발사체 데미지: " + projectile1 + ChatColor.GRAY + " ~ " + projectile2);
                lore.add(ChatColor.GREEN + "추가 공격속도: " + attackSpeed1 + ChatColor.GRAY + " ~ " + attackSpeed2);
                lore.add(ChatColor.GREEN + "추가 방어력: " + defense1 + ChatColor.GRAY + " ~ " + defense2);
                itemMeta.setLore(lore);
                tempStack.setItemMeta(itemMeta);
                return tempStack;
            }
        }
        return null;
    }

    public static void resetCraftingEquipment(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            PersistentDataContainer data = itemMeta.getPersistentDataContainer();
            List<String> lore = new ArrayList<>();
            itemMeta.setLore(lore);
            if (data.has(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI-CE-TotalKillPoint"), PersistentDataType.STRING)) {
                data.remove(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI-CE-TotalKillPoint"));
            }
            if (data.has(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI-CE-EquipmentType"), PersistentDataType.STRING)) {
                data.remove(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI-CE-EquipmentType"));
            }
            if (data.has(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI-CE-SpecialAbility"), PersistentDataType.STRING)) {
                data.remove(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI-CE-SpecialAbility"));
            }
            if (data.has(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI-CE-Melee"), PersistentDataType.STRING)) {
                data.remove(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI-CE-Melee"));
            }
            if (data.has(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI-CE-Projectile"), PersistentDataType.STRING)) {
                data.remove(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI-CE-Projectile"));
            }
            if (data.has(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI-CE-AttackSpeed"), PersistentDataType.STRING)) {
                data.remove(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI-CE-AttackSpeed"));
            }
            if (data.has(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI-CE-Defense"), PersistentDataType.STRING)) {
                data.remove(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI-CE-Defense"));
            }
            if (data.has(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI-CraftingEquipment"), PersistentDataType.STRING)) {
                data.remove(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI-CraftingEquipment"));
            }
            itemStack.setItemMeta(itemMeta);
        }
    }

    public static boolean startCraftingEquipment(Player player) {
        boolean isSuccess = false;
        CraftingInfo craftingInfo = new CraftingInfo(player);
        ItemStack itemStack = craftingInfo.getBaseItemStack();
        if (itemStack.getType() != Material.AIR) {
            int totalKillPoint = craftingInfo.getTotalKillPoint();
            if (totalKillPoint > craftingInfo.getMaxInvestKillPoint()) {
                player.sendMessage(ChatColor.RED + "투자 가능한 최대 킬 포인트가 초과되었습니다.");
            } else {
                List<String> lore = new ArrayList<>();
                lore.add("");
                ItemMeta itemMeta = itemStack.getItemMeta();
                if (itemMeta != null) {
                    PersistentDataContainer data = itemMeta.getPersistentDataContainer();
                    EquipmentType equipmentType = craftingInfo.getEquipmentType();
                    boolean isApplySpecialAbility = craftingInfo.isApplySpecialAbility();
                    int getMelee = craftingInfo.getAddMeleeDamage();
                    int getProjectile = craftingInfo.getAddProjectileDamage();
                    int getAttackSpeed = craftingInfo.getAddAttackSpeed();
                    int getDefense = craftingInfo.getAddDefense();
                    double meleeValue = 0;
                    double projectileValue = 0;
                    double attackSpeedValue = 0;
                    double defenseValue = 0;
                    data.set(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI-CE-TotalKillPoint"), PersistentDataType.STRING, String.valueOf(totalKillPoint));
                    data.set(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI-CE-EquipmentType"), PersistentDataType.STRING, equipmentType.name());
                    lore.add(ChatColor.GREEN + "장비 유형: " + ChatColor.GRAY + equipmentType.name());
                    if (isApplySpecialAbility) {
                        if (Math.random() < 0.5) {
                            if (equipmentType == EquipmentType.weapon || equipmentType == EquipmentType.armor) {
                                SpecialAbility specialAbility = getRandomAbility();
                                String info = ModPackIntegrated.specialAbilityInfoFile.getConfig().getString("Ability." + specialAbility.name());
                                if (info != null) {
                                    info = ChatColor.translateAlternateColorCodes('&', info);
                                }
                                lore.add(ChatColor.GRAY + "특수 능력: " + info);
                                data.set(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI-CE-SpecialAbility"), PersistentDataType.STRING, specialAbility.name());
                            }
                        } else {
                            lore.add(ChatColor.GRAY + "특수 능력: " + ChatColor.RED + "X");
                        }
                    }
                    for (int a = 0; a<getMelee; a++) {
                        double randomMeleeV = getRandomValue(getMeleeMaxAddRange(), getMeleeMinAddRange());
                        meleeValue += randomMeleeV;
                        double randomAttackSpeedV = getRandomValue(getAttackSpeedMaxRemoveRange(), getAttackSpeedMinRemoveRange());
                        attackSpeedValue -= randomAttackSpeedV;
                    }
                    for (int a = 0; a<getProjectile; a++) {
                        double randomProjectileV = getRandomValue(getProjectileMaxAddRange(), getProjectileMinAddRange());
                        projectileValue += randomProjectileV;
                        double randomDefenseV = getRandomValue(getDefenseMaxRemoveRange(), getDefenseMinRemoveRange());
                        defenseValue -= randomDefenseV;
                    }
                    for (int a = 0; a<getAttackSpeed; a++) {
                        double randomAttackSpeedV = getRandomValue(getAttackSpeedMaxAddRange(), getAttackSpeedMinAddRange());
                        attackSpeedValue += randomAttackSpeedV;
                        double randomMeleeV = getRandomValue(getMeleeMaxRemoveRange(), getMeleeMinRemoveRange());
                        meleeValue -= randomMeleeV;
                    }
                    for (int a = 0; a<getDefense; a++) {
                        double randomDefenseV = getRandomValue(getDefenseMaxAddRange(), getDefenseMinAddRange());
                        defenseValue += randomDefenseV;
                        int randomV = random.nextInt(2);
                        if (randomV == 0) {
                            double randomMeleeV = getRandomValue(getMeleeMaxRemoveRange(), getMeleeMinRemoveRange());
                            meleeValue -= randomMeleeV;
                        } else {
                            double randomProjectileV = getRandomValue(getProjectileMaxRemoveRange(), getProjectileMinRemoveRange());
                            projectileValue -= randomProjectileV;
                        }
                    }
                    meleeValue = Math.floor(meleeValue * 100.0) / 100.0;
                    projectileValue = Math.floor(projectileValue * 100.0) / 100.0;
                    attackSpeedValue = Math.floor(attackSpeedValue * 100.0) / 100.0;
                    defenseValue = Math.floor(defenseValue * 100.0) / 100.0;
                    String melee, projectile, attackSpeed, defense;
                    if (meleeValue > 0) {
                        melee = ChatColor.GREEN + "" + meleeValue;
                    } else if (meleeValue == 0) {
                        melee = ChatColor.WHITE + "" + meleeValue;
                    } else {
                        melee = ChatColor.RED + "" + meleeValue;
                    }
                    if (projectileValue > 0) {
                        projectile = ChatColor.GREEN + "" + projectileValue;
                    } else if (projectileValue == 0) {
                        projectile = ChatColor.WHITE + "" + projectileValue;
                    } else {
                        projectile = ChatColor.RED + "" + projectileValue;
                    }
                    if (attackSpeedValue > 0) {
                        attackSpeed = ChatColor.GREEN + "" + attackSpeedValue;
                    } else if (attackSpeedValue == 0) {
                        attackSpeed = ChatColor.WHITE + "" + attackSpeedValue;
                    } else {
                        attackSpeed = ChatColor.RED + "" + attackSpeedValue;
                    }
                    if (defenseValue > 0) {
                        defense = ChatColor.GREEN + "" + defenseValue;
                    } else if(defenseValue == 0) {
                        defense = ChatColor.WHITE + "" + defenseValue;
                    } else {
                        defense = ChatColor.RED + "" + defenseValue;
                    }
                    if (meleeValue != 0) {
                        lore.add(ChatColor.GRAY + "추가 근접 데미지: " + melee);
                    }
                    if (projectileValue != 0) {
                        lore.add(ChatColor.GRAY + "추가 발사체 데미지: " + projectile);
                    }
                    if (attackSpeedValue != 0) {
                        lore.add(ChatColor.GRAY + "추가 공격 속도: " + attackSpeed);
                    }
                    if (defenseValue != 0) {
                        lore.add(ChatColor.GRAY + "추가 방어력: " + defense);
                    }
                    data.set(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI-CE-Melee"), PersistentDataType.STRING, String.valueOf(meleeValue));
                    data.set(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI-CE-Projectile"), PersistentDataType.STRING, String.valueOf(projectileValue));
                    data.set(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI-CE-AttackSpeed"), PersistentDataType.STRING, String.valueOf(attackSpeedValue));
                    data.set(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI-CE-Defense"), PersistentDataType.STRING, String.valueOf(defenseValue));
                    itemMeta.setLore(lore);
                    itemStack.setItemMeta(itemMeta);
                    applyCraftingEquipmentTag(itemStack);
                    int slot = PlayerUtil.getPlayerEmptySlot(player);
                    if (slot != -1) {
                        player.getInventory().addItem(itemStack);
                    } else {
                        player.getWorld().dropItemNaturally(player.getLocation(), itemStack);
                    }
                    craftingInfo.resetInfo();
                }
                isSuccess = true;
            }
        }
        return isSuccess;
    }

    public static double getMeleeMinAddRange() {
        double value = 0;
        if (ModPackIntegrated.getPlugin().getConfig().contains("CraftingEquipment.MeleeDamage.AddRange")) {
            String s = ModPackIntegrated.getPlugin().getConfig().getString("CraftingEquipment.MeleeDamage.AddRange");
            if (s != null) {
                String[] a = s.split(":");
                value = Double.parseDouble(a[0]);
            }
        }
        return value;
    }

    public static double getMeleeMaxAddRange() {
        double value = 0;
        if (ModPackIntegrated.getPlugin().getConfig().contains("CraftingEquipment.MeleeDamage.AddRange")) {
            String s = ModPackIntegrated.getPlugin().getConfig().getString("CraftingEquipment.MeleeDamage.AddRange");
            if (s != null) {
                String[] a = s.split(":");
                value = Double.parseDouble(a[1]);
            }
        }
        return value;
    }

    public static double getProjectileMinAddRange() {
        double value = 0;
        if (ModPackIntegrated.getPlugin().getConfig().contains("CraftingEquipment.ProjectileDamage.AddRange")) {
            String s = ModPackIntegrated.getPlugin().getConfig().getString("CraftingEquipment.ProjectileDamage.AddRange");
            if (s != null) {
                String[] a = s.split(":");
                value = Double.parseDouble(a[0]);
            }
        }
        return value;
    }

    public static double getProjectileMaxAddRange() {
        double value = 0;
        if (ModPackIntegrated.getPlugin().getConfig().contains("CraftingEquipment.ProjectileDamage.AddRange")) {
            String s = ModPackIntegrated.getPlugin().getConfig().getString("CraftingEquipment.ProjectileDamage.AddRange");
            if (s != null) {
                String[] a = s.split(":");
                value = Double.parseDouble(a[1]);
            }
        }
        return value;
    }

    public static double getDefenseMinAddRange() {
        double value = 0;
        if (ModPackIntegrated.getPlugin().getConfig().contains("CraftingEquipment.Defense.AddRange")) {
            String s = ModPackIntegrated.getPlugin().getConfig().getString("CraftingEquipment.Defense.AddRange");
            if (s != null) {
                String[] a = s.split(":");
                value = Double.parseDouble(a[0]);
            }
        }
        return value;
    }

    public static double getDefenseMaxAddRange() {
        double value = 0;
        if (ModPackIntegrated.getPlugin().getConfig().contains("CraftingEquipment.Defense.AddRange")) {
            String s = ModPackIntegrated.getPlugin().getConfig().getString("CraftingEquipment.Defense.AddRange");
            if (s != null) {
                String[] a = s.split(":");
                value = Double.parseDouble(a[1]);
            }
        }
        return value;
    }

    public static double getAttackSpeedMinAddRange() {
        double value = 0;
        if (ModPackIntegrated.getPlugin().getConfig().contains("CraftingEquipment.AttackSpeed.AddRange")) {
            String s = ModPackIntegrated.getPlugin().getConfig().getString("CraftingEquipment.AttackSpeed.AddRange");
            if (s != null) {
                String[] a = s.split(":");
                value = Double.parseDouble(a[0]);
            }
        }
        return value;
    }

    public static double getAttackSpeedMaxAddRange() {
        double value = 0;
        if (ModPackIntegrated.getPlugin().getConfig().contains("CraftingEquipment.AttackSpeed.AddRange")) {
            String s = ModPackIntegrated.getPlugin().getConfig().getString("CraftingEquipment.AttackSpeed.AddRange");
            if (s != null) {
                String[] a = s.split(":");
                value = Double.parseDouble(a[1]);
            }
        }
        return value;
    }

    //

    public static double getMeleeMinRemoveRange() {
        double value = 0;
        if (ModPackIntegrated.getPlugin().getConfig().contains("CraftingEquipment.MeleeDamage.RemoveRange")) {
            String s = ModPackIntegrated.getPlugin().getConfig().getString("CraftingEquipment.MeleeDamage.RemoveRange");
            if (s != null) {
                String[] a = s.split(":");
                value = Double.parseDouble(a[0]);
            }
        }
        return value;
    }

    public static double getMeleeMaxRemoveRange() {
        double value = 0;
        if (ModPackIntegrated.getPlugin().getConfig().contains("CraftingEquipment.MeleeDamage.RemoveRange")) {
            String s = ModPackIntegrated.getPlugin().getConfig().getString("CraftingEquipment.MeleeDamage.RemoveRange");
            if (s != null) {
                String[] a = s.split(":");
                value = Double.parseDouble(a[1]);
            }
        }
        return value;
    }

    public static double getProjectileMinRemoveRange() {
        double value = 0;
        if (ModPackIntegrated.getPlugin().getConfig().contains("CraftingEquipment.ProjectileDamage.RemoveRange")) {
            String s = ModPackIntegrated.getPlugin().getConfig().getString("CraftingEquipment.ProjectileDamage.RemoveRange");
            if (s != null) {
                String[] a = s.split(":");
                value = Double.parseDouble(a[0]);
            }
        }
        return value;
    }

    public static double getProjectileMaxRemoveRange() {
        double value = 0;
        if (ModPackIntegrated.getPlugin().getConfig().contains("CraftingEquipment.ProjectileDamage.RemoveRange")) {
            String s = ModPackIntegrated.getPlugin().getConfig().getString("CraftingEquipment.ProjectileDamage.RemoveRange");
            if (s != null) {
                String[] a = s.split(":");
                value = Double.parseDouble(a[1]);
            }
        }
        return value;
    }

    public static double getDefenseMinRemoveRange() {
        double value = 0;
        if (ModPackIntegrated.getPlugin().getConfig().contains("CraftingEquipment.Defense.RemoveRange")) {
            String s = ModPackIntegrated.getPlugin().getConfig().getString("CraftingEquipment.Defense.RemoveRange");
            if (s != null) {
                String[] a = s.split(":");
                value = Double.parseDouble(a[0]);
            }
        }
        return value;
    }

    public static double getDefenseMaxRemoveRange() {
        double value = 0;
        if (ModPackIntegrated.getPlugin().getConfig().contains("CraftingEquipment.Defense.RemoveRange")) {
            String s = ModPackIntegrated.getPlugin().getConfig().getString("CraftingEquipment.Defense.RemoveRange");
            if (s != null) {
                String[] a = s.split(":");
                value = Double.parseDouble(a[1]);
            }
        }
        return value;
    }

    public static double getAttackSpeedMinRemoveRange() {
        double value = 0;
        if (ModPackIntegrated.getPlugin().getConfig().contains("CraftingEquipment.AttackSpeed.RemoveRange")) {
            String s = ModPackIntegrated.getPlugin().getConfig().getString("CraftingEquipment.AttackSpeed.RemoveRange");
            if (s != null) {
                String[] a = s.split(":");
                value = Double.parseDouble(a[0]);
            }
        }
        return value;
    }

    public static double getAttackSpeedMaxRemoveRange() {
        double value = 0;
        if (ModPackIntegrated.getPlugin().getConfig().contains("CraftingEquipment.AttackSpeed.RemoveRange")) {
            String s = ModPackIntegrated.getPlugin().getConfig().getString("CraftingEquipment.AttackSpeed.RemoveRange");
            if (s != null) {
                String[] a = s.split(":");
                value = Double.parseDouble(a[1]);
            }
        }
        return value;
    }

}
