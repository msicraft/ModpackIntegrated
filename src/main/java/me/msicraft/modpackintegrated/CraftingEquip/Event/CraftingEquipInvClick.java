package me.msicraft.modpackintegrated.CraftingEquip.Event;

import me.msicraft.modpackintegrated.CraftingEquip.CraftingInfo;
import me.msicraft.modpackintegrated.CraftingEquip.Enum.EquipmentType;
import me.msicraft.modpackintegrated.CraftingEquip.Inventory.CraftingEquipInv;
import me.msicraft.modpackintegrated.CraftingEquip.Util.CraftingEquipUtil;
import me.msicraft.modpackintegrated.KillPoint.KillPointUtil;
import me.msicraft.modpackintegrated.ModPackIntegrated;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class CraftingEquipInvClick implements Listener {

    private void sendKillPointMessage(Player player, int requiredPoint) {
        KillPointUtil.addKillPoint(player, -requiredPoint);
        player.closeInventory();
        player.sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "킬포인트 -" + requiredPoint);
        player.sendMessage(ChatColor.GREEN + "남은 킬포인트: " + ChatColor.GRAY + KillPointUtil.getKillPoint(player));
    }

    @EventHandler
    public void onClickMainMenu(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) { return; }
        Player player = (Player) e.getWhoClicked();
        if (e.getView().getTitle().equalsIgnoreCase(player.getName() + "'s crafting equipment")) {
            if (e.getCurrentItem() == null) { return; }
            e.setCancelled(true);
            CraftingEquipInv craftingEquipInv = new CraftingEquipInv(player);
            ItemStack itemStack = e.getCurrentItem();
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta != null) {
                CraftingInfo craftingInfo = new CraftingInfo(player);
                PersistentDataContainer data = itemMeta.getPersistentDataContainer();
                InventoryType clickInventoryType = e.getClickedInventory().getType();
                switch (clickInventoryType) {
                    case PLAYER -> {
                        int slot = e.getSlot();
                        CraftingEquipUtil.sendPlayerToCraftingInfo(player, itemStack, slot);
                        player.openInventory(craftingEquipInv.getInventory());
                        craftingEquipInv.setCraftingMenu(player);
                    }
                    case CHEST -> {
                        int slot = e.getSlot();
                        if (slot == 12) {
                            CraftingEquipUtil.sendCraftingInfoToPlayer(player);
                            player.openInventory(craftingEquipInv.getInventory());
                            craftingEquipInv.setCraftingMenu(player);
                        }
                    }
                }
                if (data.has(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI-CraftingEquipment"), PersistentDataType.STRING)) {
                    String var = data.get(new NamespacedKey(ModPackIntegrated.getPlugin(), "MPI-CraftingEquipment"), PersistentDataType.STRING);
                    if (var != null) {
                        boolean isRefresh = true;
                        switch (var) {
                            case "Start" -> {
                                isRefresh = false;
                                int totalKillPoint = craftingInfo.getTotalKillPoint();
                                if (totalKillPoint > craftingInfo.getMaxInvestKillPoint()) {
                                    player.sendMessage(ChatColor.RED + "투자 가능한 최대 킬 포인트를 초과했습니다.");
                                    player.sendMessage(ChatColor.RED + "최대: " + ChatColor.GREEN + craftingInfo.getMaxInvestKillPoint());
                                } else {
                                    if (KillPointUtil.hasKillPoint(player, totalKillPoint)) {
                                        if (CraftingEquipUtil.startCraftingEquipment(player)) {
                                            sendKillPointMessage(player, totalKillPoint);
                                            player.closeInventory();
                                            player.sendMessage(ChatColor.GREEN + "장비가 제작 되었습니다.");
                                        } else {
                                            player.sendMessage(ChatColor.RED + "알수 없는 이유로 장비 제작에 실패했습니다.");
                                            player.closeInventory();
                                        }
                                    } else {
                                        player.sendMessage(ChatColor.RED + "충분한 킬 포인트를 가지고 있지 않습니다.");
                                    }
                                }
                            }
                            case "equipmentType" -> {
                                if (e.isLeftClick()) {
                                    EquipmentType equipmentType = craftingInfo.getEquipmentType();
                                    if (equipmentType == EquipmentType.weapon) {
                                        craftingInfo.setEquipmentType(EquipmentType.armor);
                                    } else if (equipmentType == EquipmentType.armor) {
                                        craftingInfo.setEquipmentType(EquipmentType.weapon);
                                    }
                                }
                            }
                            case "applySpecialAbility" -> {
                                if (e.isLeftClick()) {
                                    boolean value = craftingInfo.isApplySpecialAbility();
                                    craftingInfo.setApplySpecialAbility(!value);
                                }
                            }
                            case "addMeleeDamage" -> {
                                int value = craftingInfo.getAddMeleeDamage();
                                if (e.isLeftClick()) {
                                    int cal = value + 1;
                                    craftingInfo.setAddMeleeDamage(cal);
                                } else if (e.isRightClick()) {
                                    int cal = value - 1;
                                    if (cal <= 0) {
                                        cal = 0;
                                    }
                                    craftingInfo.setAddMeleeDamage(cal);
                                }
                            }
                            case "addProjectileDamage" -> {
                                int value = craftingInfo.getAddProjectileDamage();
                                if (e.isLeftClick()) {
                                    int cal = value + 1;
                                    craftingInfo.setAddProjectileDamage(cal);
                                } else if (e.isRightClick()) {
                                    int cal = value - 1;
                                    if (cal <= 0) {
                                        cal = 0;
                                    }
                                    craftingInfo.setAddProjectileDamage(cal);
                                }
                            }
                            case "addAttackSpeed" -> {
                                int value = craftingInfo.getAddAttackSpeed();
                                if (e.isLeftClick()) {
                                    int cal = value + 1;
                                    craftingInfo.setAddAttackSpeed(cal);
                                } else if (e.isRightClick()) {
                                    int cal = value - 1;
                                    if (cal <= 0) {
                                        cal = 0;
                                    }
                                    craftingInfo.setAddAttackSpeed(cal);
                                }
                            }
                            case "addDefense" -> {
                                int value = craftingInfo.getAddDefense();
                                if (e.isLeftClick()) {
                                    int cal = value + 1;
                                    craftingInfo.setAddDefense(cal);
                                } else if (e.isRightClick()) {
                                    int cal = value - 1;
                                    if (cal <= 0) {
                                        cal = 0;
                                    }
                                    craftingInfo.setAddDefense(cal);
                                }
                            }
                        }
                        if (isRefresh) {
                            craftingInfo.saveInfo();
                            player.openInventory(craftingEquipInv.getInventory());
                            craftingEquipInv.setCraftingMenu(player);
                        }
                    }
                }
            }
        }
    }

}
