package me.msicraft.modpackintegrated.MainMenu.Event;

import me.msicraft.modpackintegrated.CraftingEquip.Enum.SpecialAbility;
import me.msicraft.modpackintegrated.CraftingEquip.Inventory.CraftingEquipInv;
import me.msicraft.modpackintegrated.CraftingEquip.Util.ExtractionCraftingEquipmentUtil;
import me.msicraft.modpackintegrated.KillPoint.KillPointUtil;
import me.msicraft.modpackintegrated.MainMenu.ExportEnchant.ExportEnchantUtil;
import me.msicraft.modpackintegrated.MainMenu.Inventory.MainMenuInv;
import me.msicraft.modpackintegrated.MainMenu.KillPointShop.KillPointShopUtil;
import me.msicraft.modpackintegrated.MainMenu.KillPointShop.Skill.KillPointShopSkill;
import me.msicraft.modpackintegrated.ModPackIntegrated;
import me.msicraft.modpackintegrated.PlayerData.File.PlayerDataFile;
import me.msicraft.modpackintegrated.Version.Version_1_16_R3;
import me.msicraft.modpackintegrated.Version.Version_1_18_R2;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;


public class MainMenuEvent implements Listener {

    private final KillPointShopUtil killPointShopUtil = new KillPointShopUtil();
    private final ExportEnchantUtil exportEnchantUtil = new ExportEnchantUtil();

    private void sendKillPointMessage(Player player, int requiredPoint) {
        KillPointUtil.addKillPoint(player, -requiredPoint);
        player.closeInventory();
        player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "킬포인트 -" + requiredPoint);
        player.sendMessage(ChatColor.GREEN + "남은 킬포인트: " + ChatColor.GRAY + KillPointUtil.getKillPoint(player));
    }

    private int getSpecialAbilityPage(Player player) {
        int p = 0;
        PlayerDataFile playerDataFile = new PlayerDataFile(player);
        if (playerDataFile.hasConfigFile()) {
            p = playerDataFile.getConfig().contains("SpecialAbilityList-Page") ? playerDataFile.getConfig().getInt("SpecialAbilityList-Page") : 0;
        }
        return p;
    }

    @EventHandler
    public void onClickMainMenu(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) { return; }
        Player player = (Player) e.getWhoClicked();
        if (e.getView().getTitle().equalsIgnoreCase(player.getName() + "'s main menu")) {
            ClickType type = e.getClick();
            if (type == ClickType.NUMBER_KEY) {
                e.setCancelled(true);
                return;
            }
            if (e.getCurrentItem() == null) {
                return;
            }
            e.setCancelled(true);
            MainMenuInv mainMenuInv = new MainMenuInv(player);
            ItemStack selectItem = e.getCurrentItem();
            ItemMeta selectItemMeta = selectItem.getItemMeta();
            if (selectItemMeta != null) {
                PersistentDataContainer data = selectItemMeta.getPersistentDataContainer();
                if (data.has(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI_MainMenu"), PersistentDataType.STRING)) {
                    String var = data.get(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI_MainMenu"), PersistentDataType.STRING);
                    if (var != null) {
                        switch (var) {
                            case "KillPointRelated" -> {
                                player.openInventory(mainMenuInv.getInventory());
                                mainMenuInv.setKillPointRelated(player);
                            }
                            case "ExportEnchantment" -> {
                                player.openInventory(mainMenuInv.getInventory());
                                mainMenuInv.setExportEnchantment(player);
                            }
                            case "CraftingEquipment" -> {
                                CraftingEquipInv craftingEquipInv = new CraftingEquipInv(player);
                                player.openInventory(craftingEquipInv.getInventory());
                                craftingEquipInv.setCraftingMenu(player);
                            }
                            case "SpecialAbilityInfoList" -> {
                                player.openInventory(mainMenuInv.getInventory());
                                mainMenuInv.setSpecialAbilityInfo(player);
                            }
                            case "ExtractionCraftingEquipment" -> {
                                player.openInventory(mainMenuInv.getInventory());
                                mainMenuInv.setExtractInv(player);
                            }
                            case "PersonalOption" -> {
                                player.openInventory(mainMenuInv.getInventory());
                                mainMenuInv.setPersonalOption(player);
                            }
                        }
                    }
                } else if (data.has(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI_KillPointRelated"), PersistentDataType.STRING)) {
                    String var = data.get(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI_KillPointRelated"), PersistentDataType.STRING);
                    if (var != null && e.isLeftClick()) {
                        int requiredPoint = killPointShopUtil.getRequiredKillPoint(var);
                        switch (var) {
                            case "MoveSpawn" -> {
                                if (KillPointUtil.hasKillPoint(player, requiredPoint)) {
                                    if (KillPointShopSkill.moveSpawn(player)) {
                                        player.closeInventory();
                                        sendKillPointMessage(player, requiredPoint);
                                    } else {
                                        player.sendMessage(ChatColor.RED + "스폰 위치가 없습니다.");
                                    }
                                } else {
                                    player.sendMessage(ChatColor.RED + "충분한 킬포인트를 가지고 있지 않습니다.");
                                }
                            }
                            case "BackDeathLocation" -> {
                                if (KillPointUtil.hasKillPoint(player, requiredPoint)) {
                                    if (KillPointShopSkill.teleportLastDeathLocation(player)) {
                                        player.closeInventory();
                                        sendKillPointMessage(player, requiredPoint);
                                    } else {
                                        player.sendMessage(ChatColor.RED + "죽은 위치가 존재하지 않습니다.");
                                    }
                                } else {
                                    player.sendMessage(ChatColor.RED + "충분한 킬포인트를 가지고 있지 않습니다.");
                                }
                            }
                            case "RerollSpecialAbility" -> {
                                if (KillPointUtil.hasKillPoint(player, requiredPoint)) {
                                    if (KillPointShopSkill.reRollSpecialAbility(player)) {
                                        player.closeInventory();
                                        sendKillPointMessage(player, requiredPoint);
                                    }
                                } else {
                                    player.sendMessage(ChatColor.RED + "충분한 킬포인트를 가지고 있지 않습니다.");
                                }
                            }
                            case "ApplySpecialAbility" -> {
                                if (KillPointUtil.hasKillPoint(player, requiredPoint)) {
                                    if (KillPointShopSkill.applySpecialAbility(player)) {
                                        player.closeInventory();
                                        sendKillPointMessage(player, requiredPoint);
                                    }
                                } else {
                                    player.sendMessage(ChatColor.RED + "충분한 킬포인트를 가지고 있지 않습니다.");
                                }
                            }
                            case "ResetCraftingInfo" -> {
                                if (KillPointUtil.hasKillPoint(player, requiredPoint)) {
                                    if (KillPointShopSkill.resetCraftingInfo(player)) {
                                        player.closeInventory();
                                        sendKillPointMessage(player, requiredPoint);
                                    }
                                } else {
                                    player.sendMessage(ChatColor.RED + "충분한 킬포인트를 가지고 있지 않습니다.");
                                }
                            }
                        }
                    }
                } else if (data.has(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI_ExportEnchant"), PersistentDataType.STRING)) {
                    String var = data.get(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI_ExportEnchant"), PersistentDataType.STRING);
                    if (var != null && e.isLeftClick()) {
                        switch (var) {
                            case "Start-Export" -> {
                                ItemStack slotItem = ModPackIntegrated.exportEnchantMap.get(player.getUniqueId());
                                if (slotItem.getType() != Material.AIR) {
                                    int getEnchantSize = exportEnchantUtil.getEnchantAmount(slotItem);
                                    if (getEnchantSize >= 1) {
                                        if (KillPointUtil.hasKillPoint(player, getEnchantSize)) {
                                            if (exportEnchantUtil.startExportEnchant(player)) {
                                                player.sendMessage(ChatColor.GREEN + "성공적으로 인챈트가 추출 되었습니다.");
                                                sendKillPointMessage(player, getEnchantSize);
                                            } else {
                                                player.sendMessage(ChatColor.RED + "추츨 실패");
                                            }
                                        } else {
                                            player.sendMessage(ChatColor.RED + "충분한 킬포인트를 가지고 있지 않습니다.");
                                        }
                                    } else {
                                        player.sendMessage(ChatColor.RED + "추출할 아이템에 인챈트가 없습니다.");
                                    }
                                } else {
                                    player.sendMessage(ChatColor.RED + "추출 아이템이 없습니다.");
                                }
                            }
                            case "Start-Full-Export" -> {
                                ItemStack slotItem = ModPackIntegrated.exportEnchantMap.get(player.getUniqueId());
                                if (slotItem.getType() != Material.AIR) {
                                    int getEnchantSize = exportEnchantUtil.getEnchantAmount(slotItem);
                                    if (getEnchantSize >= 1) {
                                        int requiredPoint = getEnchantSize * 5;
                                        if (KillPointUtil.hasKillPoint(player, requiredPoint)) {
                                            if (exportEnchantUtil.startFullExportEnchant(player)) {
                                                player.sendMessage(ChatColor.GREEN + "모든 인챈트가 추출 되었습니다.");
                                                sendKillPointMessage(player, requiredPoint);
                                            } else {
                                                player.sendMessage(ChatColor.RED + "추츨 실패");
                                            }
                                        } else {
                                            player.sendMessage(ChatColor.RED + "충분한 킬포인트를 가지고 있지 않습니다.");
                                        }
                                    } else {
                                        player.sendMessage(ChatColor.RED + "추출할 아이템에 인챈트가 없습니다.");
                                    }
                                } else {
                                    player.sendMessage(ChatColor.RED + "추출 아이템이 없습니다.");
                                }
                            }
                        }
                    }
                } else if (data.has(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI-SpecialAbilityList"), PersistentDataType.STRING)) {
                    String var = data.get(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI-SpecialAbilityList"), PersistentDataType.STRING);
                    if (var != null) {
                        PlayerDataFile playerDataFile = new PlayerDataFile(player);
                        int page = getSpecialAbilityPage(player);
                        int maxPage = SpecialAbility.values().length / 45;
                        switch (var) {
                            case "next" -> {
                                int nextPage = page + 1;
                                if (nextPage > maxPage) {
                                    nextPage = 0;
                                }
                                if (page != nextPage) {
                                    playerDataFile.getConfig().set("SpecialAbilityList-Page", nextPage);
                                    playerDataFile.saveConfig();
                                    player.openInventory(mainMenuInv.getInventory());
                                    mainMenuInv.setSpecialAbilityInfo(player);
                                }
                            }
                            case "previous" -> {
                                int nextPage = page - 1;
                                if (nextPage < 0) {
                                    nextPage = maxPage;
                                }
                                if (page != nextPage) {
                                    playerDataFile.getConfig().set("SpecialAbilityList-Page", nextPage);
                                    playerDataFile.saveConfig();
                                    player.openInventory(mainMenuInv.getInventory());
                                    mainMenuInv.setSpecialAbilityInfo(player);
                                }
                            }
                        }
                    }
                } else if (data.has(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI_PersonalOption"), PersistentDataType.STRING)) {
                    String var = data.get(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI_PersonalOption"), PersistentDataType.STRING);
                    if (var != null && e.isLeftClick()) {
                        PlayerDataFile dataFile = new PlayerDataFile(player);
                        switch (var) {
                            case "MenuQuickOpen" -> {
                                boolean quickOpen = dataFile.getConfig().contains("Option.MenuQuickOpen") && dataFile.getConfig().getBoolean("Option.MenuQuickOpen");
                                if (quickOpen) {
                                    dataFile.getConfig().set("Option.MenuQuickOpen", false);
                                } else {
                                    dataFile.getConfig().set("Option.MenuQuickOpen", true);
                                }
                            }
                            case "DisplayDotDamage" -> {
                                boolean displayDotDamage = dataFile.getConfig().contains("Option.DisplayDotDamage") && dataFile.getConfig().getBoolean("Option.DisplayDotDamage");
                                if (displayDotDamage) {
                                    dataFile.getConfig().set("Option.DisplayDotDamage", false);
                                } else {
                                    dataFile.getConfig().set("Option.DisplayDotDamage", true);
                                }
                            }
                            case "DisplayBackAttack" -> {
                                boolean displayBackAttack = dataFile.getConfig().contains("Option.DisplayBackAttack") && dataFile.getConfig().getBoolean("Option.DisplayBackAttack");
                                if (displayBackAttack) {
                                    dataFile.getConfig().set("Option.DisplayBackAttack", false);
                                } else {
                                    dataFile.getConfig().set("Option.DisplayBackAttack", true);
                                }
                            }
                        }
                        dataFile.saveConfig();
                        player.openInventory(mainMenuInv.getInventory());
                        mainMenuInv.setPersonalOption(player);
                    }
                }
            }
            ItemStack infoBook = e.getInventory().getItem(4);
            if (infoBook != null && infoBook.getType() == Material.BOOK) {
                if (e.isShiftClick()) {
                    ItemStack temp = new ItemStack(infoBook);
                    TextComponent main = new TextComponent(ChatColor.WHITE + player.getName() + ": ");
                    main.setColor(net.md_5.bungee.api.ChatColor.WHITE);
                    TextComponent sub = new TextComponent("[" + ChatColor.AQUA + "플레이어 정보" + ChatColor.WHITE + "]");
                    HoverEvent hoverEvent = null;
                    if (ModPackIntegrated.bukkitVersion.equals("1.16.5")) {
                        hoverEvent = Version_1_16_R3.getHoverEventByShowItem(temp);
                    } else if (ModPackIntegrated.bukkitVersion.equals("1.18.2")) {
                        hoverEvent = Version_1_18_R2.getHoverEventByShowItem(temp);
                    }
                    if (hoverEvent != null) {
                        sub.setHoverEvent(hoverEvent);
                        main.addExtra(sub);
                        Bukkit.spigot().broadcast(main);
                    }
                }
            }
            ItemStack exportButton = e.getInventory().getItem(31);
            if (exportButton != null && exportButton.getType() == Material.STONE) {
                ItemMeta exportButtonMeta = exportButton.getItemMeta();
                if (exportButtonMeta != null) {
                    PersistentDataContainer data = exportButtonMeta.getPersistentDataContainer();
                    if (data.has(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI_ExportEnchant"), PersistentDataType.STRING)) {
                        ItemStack clickItem = e.getCurrentItem();
                        if (clickItem != null && clickItem.getType() != Material.AIR) {
                            InventoryType clickInventoryType = e.getClickedInventory().getType();
                            switch (clickInventoryType) {
                                case PLAYER -> {
                                    int slot = e.getSlot();
                                    exportEnchantUtil.sendPlayerToMap(player, slot, clickItem);
                                    player.openInventory(mainMenuInv.getInventory());
                                    mainMenuInv.setExportEnchantment(player);
                                }
                                case CHEST -> {
                                    int slot = e.getSlot();
                                    if (slot == 22) {
                                        exportEnchantUtil.sendMapToPlayer(player, slot);
                                        player.openInventory(mainMenuInv.getInventory());
                                        mainMenuInv.setExportEnchantment(player);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            ItemStack extractionButton = e.getInventory().getItem(53);
            if (extractionButton != null && extractionButton.getType() == Material.STONE) {
                ItemMeta itemMeta = extractionButton.getItemMeta();
                if (itemMeta != null) {
                    PersistentDataContainer data = itemMeta.getPersistentDataContainer();
                    if (data.has(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI-Extraction-Start"), PersistentDataType.STRING)) {
                        int clickSlot = e.getSlot();
                        InventoryType inventoryType = e.getClickedInventory().getType();
                        switch (inventoryType) {
                            case CHEST -> {
                                if (clickSlot == 53) {
                                    String d = data.get(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI-Extraction-Start"), PersistentDataType.STRING);
                                    if (d != null && d.equals("Start")) {
                                        ExtractionCraftingEquipmentUtil.startExtraction(player);
                                    }
                                } else {
                                    ExtractionCraftingEquipmentUtil.sendStorageToPlayer(player, clickSlot);
                                    player.openInventory(mainMenuInv.getInventory());
                                    mainMenuInv.setExtractInv(player);
                                }
                            }
                            case PLAYER -> {
                                ExtractionCraftingEquipmentUtil.sendPlayerToStorage(player, selectItem, clickSlot);
                                player.openInventory(mainMenuInv.getInventory());
                                mainMenuInv.setExtractInv(player);
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void quickMenuOpen(PlayerSwapHandItemsEvent e) {
        Player player = e.getPlayer();
        PlayerDataFile dataFile = new PlayerDataFile(player);
        boolean quickOpen = dataFile.getConfig().contains("Option.MenuQuickOpen") && dataFile.getConfig().getBoolean("Option.MenuQuickOpen");
        if (quickOpen) {
            if (player.isSneaking()) {
                e.setCancelled(true);
                MainMenuInv mainMenuInv = new MainMenuInv(player);
                player.openInventory(mainMenuInv.getInventory());
                mainMenuInv.setMainMenuInv(player);
            }
        }
    }

}
