package me.msicraft.modpackintegrated.CraftingEquip.Util;

import me.msicraft.modpackintegrated.CraftingEquip.Data.ExtractionInfo;
import me.msicraft.modpackintegrated.KillPoint.KillPointUtil;
import me.msicraft.modpackintegrated.Util.PlayerUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ExtractionCraftingEquipmentUtil {

    private static final ItemStack airItemStack = new ItemStack(Material.AIR, 1);

    public static void sendPlayerToStorage(Player player, ItemStack itemStack, int playerSlot) {
        ExtractionInfo extractionInfo = new ExtractionInfo(player);
        int emptySpace = extractionInfo.getEmptySpace();
        if (emptySpace != -1) {
            extractionInfo.setItemStack(emptySpace, itemStack);
            player.getInventory().setItem(playerSlot, airItemStack);
        } else {
            player.sendMessage(ChatColor.RED + "빈 공간이 없습니다.");
        }
    }

    public static void sendStorageToPlayer(Player player, int storageSlot) {
        ExtractionInfo extractionInfo = new ExtractionInfo(player);
        ItemStack itemStack = extractionInfo.getItemStack(storageSlot);
        if (itemStack.getType() != Material.AIR) {
            int emptyPlayerSlot = PlayerUtil.getPlayerEmptySlot(player);
            if (emptyPlayerSlot != -1) {
                player.getInventory().addItem(itemStack);
                extractionInfo.setItemStack(storageSlot, airItemStack);
            } else {
                player.sendMessage(ChatColor.RED + "인벤토리에 빈 공간이 없습니다.");
            }
        }
    }

    public static int getTotalKillPoints(Player player) {
        int v = 0;
        ExtractionInfo extractionInfo = new ExtractionInfo(player);
        int maxSize = extractionInfo.getMaxSize();
        for (int a = 0; a<maxSize; a++) {
            ItemStack itemStack = extractionInfo.getItemStack(a);
            if (itemStack.getType() != Material.AIR) {
                int totalKillPoint = CraftingEquipUtil.getTotalKillPoint(itemStack);
                int cal = totalKillPoint / 2;
                v = v + cal;
            }
        }
        return v;
    }

    public static void startExtraction(Player player) {
        ExtractionInfo extractionInfo = new ExtractionInfo(player);
        int totalPoint = getTotalKillPoints(player);
        KillPointUtil.addKillPoint(player, totalPoint);
        player.closeInventory();
        player.sendMessage(ChatColor.BOLD + "" + ChatColor.GREEN + "킬포인트 +" + totalPoint);
        player.sendMessage(ChatColor.GREEN + "현재 킬포인트: " + ChatColor.GRAY + KillPointUtil.getKillPoint(player));
        extractionInfo.resetInfo();
    }

}
