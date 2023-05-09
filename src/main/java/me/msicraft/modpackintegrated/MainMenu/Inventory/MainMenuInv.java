package me.msicraft.modpackintegrated.MainMenu.Inventory;

import me.msicraft.modpackintegrated.CraftingEquip.Enum.SpecialAbility;
import me.msicraft.modpackintegrated.KillPoint.KillPointUtil;
import me.msicraft.modpackintegrated.MainMenu.ExportEnchant.ExportEnchantUtil;
import me.msicraft.modpackintegrated.MainMenu.KillPointShop.KillPointShopUtil;
import me.msicraft.modpackintegrated.MainMenu.MainMenuUtil;
import me.msicraft.modpackintegrated.ModPackIntegrated;
import me.msicraft.modpackintegrated.PlayerData.File.PlayerDataFile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class MainMenuInv implements InventoryHolder {

    private Inventory mainMenuInv;

    private final MainMenuUtil mainMenuUtil = new MainMenuUtil();
    private final KillPointShopUtil killPointShopUtil = new KillPointShopUtil();

    public MainMenuInv(Player player) {
        mainMenuInv = Bukkit.createInventory(player, 54, player.getName() + "'s main menu");
    }

    public void setMainMenuInv(Player player) {
        setInfo(player);
        menuSetting();
    }

    private void setInfo(Player player) {
        List<String> list = new ArrayList<>();
        ItemStack itemStack = new ItemStack(Material.BOOK, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "플레이어 정보");
        double killPointExpPercent = mainMenuUtil.getKillPointNextLevelToExpPercent(player);
        list.add("");
        list.add(ChatColor.GREEN + "킬 포인트: " + ChatColor.GRAY + KillPointUtil.getKillPoint(player) + " (" + (int) killPointExpPercent + "%)");
        itemMeta.setLore(list);
        itemStack.setItemMeta(itemMeta);
        mainMenuInv.setItem(4, itemStack);
    }

    private void menuSetting() {
        String mainTag = "MPI_MainMenu";
        ItemStack itemStack;
        List<String> list = new ArrayList<>();
        itemStack = createNormalItem(Material.HOPPER, ChatColor.WHITE + "킬 포인트 상점", list, mainTag, "KillPointRelated");
        mainMenuInv.setItem(10, itemStack);
        itemStack = createNormalItem(Material.ANVIL, ChatColor.WHITE + "장비 인챈트 추출", list, mainTag, "ExportEnchantment");
        mainMenuInv.setItem(11, itemStack);
        itemStack = createNormalItem(Material.CRAFTING_TABLE, ChatColor.WHITE + "장비 제작", list, mainTag, "CraftingEquipment");
        mainMenuInv.setItem(12, itemStack);
        itemStack = createNormalItem(Material.PAPER, ChatColor.WHITE + "장비 특수능력 목록", list, mainTag, "SpecialAbilityInfoList");
        mainMenuInv.setItem(19, itemStack);
    }

    private static int[] shopSlots = {10,11,12,13,14,15,16, 19,20,21,22,23,24,25, 28,29,30,31,32,33,34, 37,38,39,40,41,42,43};

    public void setKillPointRelated(Player player) {
        String killPointTag = "MPI_KillPointRelated";
        setInfo(player);
        ItemStack itemStack;
        List<String> internalNames = killPointShopUtil.getShopItemInternalNames();
        int count = 0;
        for (String internalName : internalNames) {
            if (killPointShopUtil.isEnabled(internalName)) {
                int slot = shopSlots[count];
                Material material = killPointShopUtil.getMaterial(internalName);
                String name = killPointShopUtil.getName(internalName);
                List<String> lore = killPointShopUtil.getLoreList(internalName);
                itemStack = createNormalItem(material, name, lore, killPointTag, internalName);
                mainMenuInv.setItem(slot, itemStack);
                count++;
            }
        }
    }

    private final int exportEnchant_itemSlot = 22;
    private final ExportEnchantUtil exportEnchantUtil = new ExportEnchantUtil();

    public void setExportEnchantment(Player player) {
        String dataTag = "MPI_ExportEnchant";
        setInfo(player);
        ItemStack itemStack;
        List<String> temp = new ArrayList<>();
        ItemStack emptySlot = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
        ItemMeta itemMeta = emptySlot.getItemMeta();
        itemMeta.setDisplayName("");
        emptySlot.setItemMeta(itemMeta);
        ItemStack exportItem = ModPackIntegrated.exportEnchantMap.get(player.getUniqueId());
        mainMenuInv.setItem(exportEnchant_itemSlot, exportItem);
        if (exportItem.getType() != Material.AIR) {
            temp.add(ChatColor.GREEN + "필요한 킬 포인트: " + ChatColor.GRAY + exportEnchantUtil.getEnchantAmount(exportItem));
        } else {
            temp.add(ChatColor.GREEN + "필요한 킬 포인트: " + ChatColor.GRAY + "0");
        }
        temp.add("");
        temp.add(ChatColor.RED + "추출시 추출 아이템은 사라집니다.");
        itemStack = createNormalItem(Material.STONE, ChatColor.WHITE + "추출", temp, dataTag, "Start-Export");
        mainMenuInv.setItem(31 ,itemStack);
        if (!temp.isEmpty()) {temp.clear();}
        temp.add(ChatColor.GREEN + "5배의 킬 포인트를 사용해서 모든 인챈트 추출");
        temp.add("");
        if (exportItem.getType() != Material.AIR) {
            temp.add(ChatColor.GREEN + "필요한 킬 포인트: " + ChatColor.GRAY + (exportEnchantUtil.getEnchantAmount(exportItem)*5));
        } else {
            temp.add(ChatColor.GREEN + "필요한 킬 포인트: " + ChatColor.GRAY + "0");
        }
        itemStack = createNormalItem(Material.ENCHANTED_BOOK, ChatColor.WHITE + "완벽 추출", temp, dataTag, "Start-Full-Export");
        mainMenuInv.setItem(32, itemStack);
        for (int a = 0; a<mainMenuInv.getSize(); a++) {
            if (a == 22) {
                continue;
            }
            ItemStack slot = mainMenuInv.getItem(a);
            if (slot == null || slot.getType() == Material.AIR) {
                mainMenuInv.setItem(a, emptySlot);
            }
        }
    }

    private final int[] abilityInfoSlots = {0,1,2,3,4,5,6,7,8,9, 10,11,12,13,14,15,16,17, 18,19,20,21,22,23,24,25,26, 27,28,29,30,31,32,33,34,35, 36,37,38,39,40,41,42,44};

    private int getSpecialAbilityPage(Player player) {
        int p = 0;
        PlayerDataFile playerDataFile = new PlayerDataFile(player);
        if (playerDataFile.hasConfigFile()) {
            p = playerDataFile.getConfig().contains("SpecialAbilityList-Page") ? playerDataFile.getConfig().getInt("SpecialAbilityList-Page") : 0;
        }
        return p;
    }

    private void setSpecialAbilityPageBook(Player player) {
        ItemStack itemStack = new ItemStack(Material.ARROW, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName("다음");
            itemMeta.getPersistentDataContainer().set(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI-SpecialAbilityList"), PersistentDataType.STRING, "next");
            itemStack.setItemMeta(itemMeta);
            mainMenuInv.setItem(50, itemStack);
            itemMeta.setDisplayName("이전");
            itemMeta.getPersistentDataContainer().set(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI-SpecialAbilityList"), PersistentDataType.STRING, "previous");
            itemStack.setItemMeta(itemMeta);
            mainMenuInv.setItem(48, itemStack);
        }
        itemStack = new ItemStack(Material.BOOK, 1);
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            int count = getSpecialAbilityPage(player) + 1;
            meta.setDisplayName(ChatColor.WHITE + "Page: " + count);
            itemStack.setItemMeta(meta);
            mainMenuInv.setItem(49, itemStack);
        }
    }

    public void setSpecialAbilityInfo(Player player) {
        setSpecialAbilityPageBook(player);
        List<SpecialAbility> specialAbilities = List.of(SpecialAbility.values());
        ItemStack itemStack;
        List<String> list = new ArrayList<>();
        int count = 0;
        int maxSize = specialAbilities.size();
        int page = getSpecialAbilityPage(player);
        int lastCount = page*45;
        for (int a = lastCount; a<maxSize; a++) {
            if (!list.isEmpty()) {
                list.clear();
            }
            SpecialAbility specialAbility = specialAbilities.get(a);
            String info = ModPackIntegrated.specialAbilityInfoFile.getConfig().getString("Ability." + specialAbility.name());
            if (info != null) {
                info = ChatColor.translateAlternateColorCodes('&', info);
                list.add(info);
                itemStack = createNormalItem(Material.PAPER, ChatColor.WHITE + "" + count, list, "dd", "dd");
                mainMenuInv.setItem(count, itemStack);
                count++;
            }
            if (count >= 45) {
                break;
            }
        }
    }

    private ItemStack createNormalItem(Material material, String name, List<String> list, String dataTag, String data) {
        ItemStack itemStack = new ItemStack(material, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(name);
        itemMeta.setLore(list);
        PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
        dataContainer.set(new NamespacedKey(ModPackIntegrated.getPlugin(), dataTag), PersistentDataType.STRING, data);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    @Override
    public Inventory getInventory() {
        return mainMenuInv;
    }
}
