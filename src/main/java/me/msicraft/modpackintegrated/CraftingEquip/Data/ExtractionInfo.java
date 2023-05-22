package me.msicraft.modpackintegrated.CraftingEquip.Data;

import me.msicraft.modpackintegrated.PlayerData.File.PlayerDataFile;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ExtractionInfo {

    private Player player;
    private PlayerDataFile dataFile;
    private final ItemStack airStack = new ItemStack(Material.AIR, 1);
    private final int maxSize = 45;

    public ExtractionInfo(Player player) {
        this.player = player;
        dataFile = new PlayerDataFile(player);
        if (dataFile.hasConfigFile()) {
            if (!dataFile.getConfig().contains("ExtractionCraftingEquipment.ItemStack")) {
                for (int a = 0; a<maxSize; a++) {
                    if (!dataFile.getConfig().contains("ExtractionCraftingEquipment.ItemStack." + a)) {
                        dataFile.getConfig().set("ExtractionCraftingEquipment.ItemStack." + a, airStack);
                    }
                }
                dataFile.saveConfig();
            }
        }
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void resetInfo() {
        if (dataFile.hasConfigFile()) {
            for (int a = 0; a < maxSize; a++) {
                dataFile.getConfig().set("ExtractionCraftingEquipment.ItemStack." + a, airStack);
            }
            dataFile.saveConfig();
        }
    }

    public int getEmptySpace() {
        int slot = -1;
        if (dataFile.hasConfigFile()) {
            for (int a = 0; a<maxSize; a++) {
                ItemStack itemStack = getItemStack(a);
                if (itemStack.getType() == Material.AIR) {
                    slot = a;
                    break;
                }
            }
        }
        return slot;
    }

    public ItemStack getItemStack(int slot) {
        ItemStack itemStack = airStack;
        if (dataFile.hasConfigFile()) {
            if (dataFile.getConfig().contains("ExtractionCraftingEquipment.ItemStack." + slot)) {
                itemStack = dataFile.getConfig().getItemStack("ExtractionCraftingEquipment.ItemStack." + slot);
            }
        }
        return itemStack;
    }

    public void setItemStack(int slot, ItemStack itemStack) {
        if (dataFile.hasConfigFile()) {
            dataFile.getConfig().set("ExtractionCraftingEquipment.ItemStack." + slot, itemStack);
            dataFile.saveConfig();
        }
    }

}
