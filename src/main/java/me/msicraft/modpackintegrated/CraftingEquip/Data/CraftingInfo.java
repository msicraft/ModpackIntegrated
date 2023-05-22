package me.msicraft.modpackintegrated.CraftingEquip.Data;

import me.msicraft.modpackintegrated.CraftingEquip.Enum.EquipmentType;
import me.msicraft.modpackintegrated.CraftingEquip.Util.CraftingEquipUtil;
import me.msicraft.modpackintegrated.ModPackIntegrated;
import me.msicraft.modpackintegrated.PlayerData.File.PlayerDataFile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CraftingInfo {

    private Player player;
    private PlayerDataFile dataFile;
    private final ItemStack airStack = new ItemStack(Material.AIR, 1);
    private ItemStack baseItemStack = airStack; //BaseItemStack
    private boolean applySpecialAbility = false; // ApplySpecialAbility
    private EquipmentType equipmentType = EquipmentType.weapon;
    private int requiredKillPoint,addMeleeDamage,addProjectileDamage,addDefense,addAttackSpeed,addMaxHealth;
    private int maxInvestKillPoint = 0;

    public CraftingInfo(Player player) {
        this.player = player;
        dataFile = new PlayerDataFile(player);
        maxInvestKillPoint = ModPackIntegrated.getPlugin().getConfig().contains("CraftingEquipment.MaxKillPoint") ? ModPackIntegrated.getPlugin().getConfig().getInt("CraftingEquipment.MaxKillPoint") : 0;
        if (dataFile.hasConfigFile()) {
            if (dataFile.getConfig().contains("CraftingEquipment.BaseItemStack")) {
                baseItemStack = dataFile.getConfig().getItemStack("CraftingEquipment.BaseItemStack");
            }
            requiredKillPoint = dataFile.getConfig().contains("CraftingEquipment.RequiredKillPoint") ? dataFile.getConfig().getInt("CraftingEquipment.RequiredKillPoint") : 0;
            applySpecialAbility = dataFile.getConfig().contains("CraftingEquipment.ApplySpecialAbility") && dataFile.getConfig().getBoolean("CraftingEquipment.ApplySpecialAbility");
            String equipmentTypeS = dataFile.getConfig().contains("CraftingEquipment.EquipmentType") ? dataFile.getConfig().getString("CraftingEquipment.EquipmentType") : EquipmentType.weapon.name();
            equipmentType = EquipmentType.valueOf(equipmentTypeS);
            addMeleeDamage = dataFile.getConfig().contains("CraftingEquipment.AddMeleeDamage") ? dataFile.getConfig().getInt("CraftingEquipment.AddMeleeDamage") : 0;
            addProjectileDamage = dataFile.getConfig().contains("CraftingEquipment.AddProjectileDamage") ? dataFile.getConfig().getInt("CraftingEquipment.AddProjectileDamage") : 0;
            addDefense = dataFile.getConfig().contains("CraftingEquipment.AddDefense") ? dataFile.getConfig().getInt("CraftingEquipment.AddDefense") : 0;
            addAttackSpeed = dataFile.getConfig().contains("CraftingEquipment.AddAttackSpeed") ? dataFile.getConfig().getInt("CraftingEquipment.AddAttackSpeed") : 0;
            addMaxHealth = dataFile.getConfig().contains("CraftingEquipment.MaxHealth") ? dataFile.getConfig().getInt("CraftingEquipment.MaxHealth") : 0;
        } else {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "플레이어 데이터가 존재하지 않음: " + ChatColor.GREEN + player.getName() + "\n" + player.getUniqueId());
            player.kickPlayer(ChatColor.RED + "플레이어 데이터가 존재하지 않습니다\n재접속 해주시기 바랍니다.");
        }
    }

    public void saveInfo() {
        dataFile.getConfig().set("CraftingEquipment.BaseItemStack", baseItemStack);
        dataFile.getConfig().set("CraftingEquipment.RequiredKillPoint", requiredKillPoint);
        dataFile.getConfig().set("CraftingEquipment.ApplySpecialAbility", applySpecialAbility);
        dataFile.getConfig().set("CraftingEquipment.AddMeleeDamage", addMeleeDamage);
        dataFile.getConfig().set("CraftingEquipment.AddProjectileDamage", addProjectileDamage);
        dataFile.getConfig().set("CraftingEquipment.AddDefense", addDefense);
        dataFile.getConfig().set("CraftingEquipment.AddAttackSpeed", addAttackSpeed);
        dataFile.getConfig().set("CraftingEquipment.MaxHealth", addMaxHealth);
        dataFile.getConfig().set("CraftingEquipment.EquipmentType", equipmentType.name());
        dataFile.saveConfig();
    }

    public void resetInfo() {
        setBaseItemStack(airStack);
        setRequiredKillPoint(0);
        setApplySpecialAbility(false);
        setAddMeleeDamage(0);
        setAddProjectileDamage(0);
        setAddAttackSpeed(0);
        setAddDefense(0);
        setAddMaxHealth(0);
        setEquipmentType(EquipmentType.weapon);
        dataFile.getConfig().set("CraftingEquipment.BaseItemStack", baseItemStack);
        dataFile.getConfig().set("CraftingEquipment.RequiredKillPoint", requiredKillPoint);
        dataFile.getConfig().set("CraftingEquipment.ApplySpecialAbility", applySpecialAbility);
        dataFile.getConfig().set("CraftingEquipment.AddMeleeDamage", addMeleeDamage);
        dataFile.getConfig().set("CraftingEquipment.AddProjectileDamage", addProjectileDamage);
        dataFile.getConfig().set("CraftingEquipment.AddDefense", addDefense);
        dataFile.getConfig().set("CraftingEquipment.AddAttackSpeed", addAttackSpeed);
        dataFile.getConfig().set("CraftingEquipment.MaxHealth", addMaxHealth);
        dataFile.getConfig().set("CraftingEquipment.EquipmentType", equipmentType.name());
        dataFile.saveConfig();
    }

    public int getTotalKillPoint() {
        int value = (addMeleeDamage * CraftingEquipUtil.requiredMeleeDamage()) + (addProjectileDamage * CraftingEquipUtil.requiredProjectileDamage())
                + (addAttackSpeed * CraftingEquipUtil.requiredAttackSpeed()) + (addDefense * CraftingEquipUtil.requiredDefense()) + (addMaxHealth * CraftingEquipUtil.requiredMaxHealth());
        if (applySpecialAbility) {
            value = value + CraftingEquipUtil.requiredSpecialAbility();
        }
        return value;
    }

    public Player getPlayer() {
        return player;
    }

    public PlayerDataFile getDataFile() {
        return dataFile;
    }

    public ItemStack getAirStack() {
        return airStack;
    }

    public ItemStack getBaseItemStack() {
        return baseItemStack;
    }

    public void setBaseItemStack(ItemStack baseItemStack) {
        this.baseItemStack = baseItemStack;
    }

    public boolean isApplySpecialAbility() {
        return applySpecialAbility;
    }

    public void setApplySpecialAbility(boolean applySpecialAbility) {
        this.applySpecialAbility = applySpecialAbility;
    }

    public EquipmentType getEquipmentType() {
        return equipmentType;
    }

    public void setEquipmentType(EquipmentType equipmentType) {
        this.equipmentType = equipmentType;
    }

    public int getRequiredKillPoint() {
        return requiredKillPoint;
    }

    public void setRequiredKillPoint(int requiredKillPoint) {
        this.requiredKillPoint = requiredKillPoint;
    }

    public int getAddMeleeDamage() {
        return addMeleeDamage;
    }

    public void setAddMeleeDamage(int addMeleeDamage) {
        this.addMeleeDamage = addMeleeDamage;
    }

    public int getAddProjectileDamage() {
        return addProjectileDamage;
    }

    public void setAddProjectileDamage(int addProjectileDamage) {
        this.addProjectileDamage = addProjectileDamage;
    }

    public int getAddDefense() {
        return addDefense;
    }

    public void setAddDefense(int addDefense) {
        this.addDefense = addDefense;
    }

    public int getAddAttackSpeed() {
        return addAttackSpeed;
    }

    public void setAddAttackSpeed(int addAttackSpeed) {
        this.addAttackSpeed = addAttackSpeed;
    }

    public int getMaxInvestKillPoint() {
        return maxInvestKillPoint;
    }

    public int getAddMaxHealth() {
        return addMaxHealth;
    }

    public void setAddMaxHealth(int addMaxHealth) {
        this.addMaxHealth = addMaxHealth;
    }
}
