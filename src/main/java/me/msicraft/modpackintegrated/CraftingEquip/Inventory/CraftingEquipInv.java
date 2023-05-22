package me.msicraft.modpackintegrated.CraftingEquip.Inventory;

import me.msicraft.modpackintegrated.CraftingEquip.Data.CraftingInfo;
import me.msicraft.modpackintegrated.CraftingEquip.Event.CraftingEquipEvent;
import me.msicraft.modpackintegrated.CraftingEquip.Util.CraftingEquipUtil;
import me.msicraft.modpackintegrated.KillPoint.KillPointUtil;
import me.msicraft.modpackintegrated.MainMenu.MainMenuUtil;
import me.msicraft.modpackintegrated.ModPackIntegrated;
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

public class CraftingEquipInv implements InventoryHolder {

    private Inventory craftingInv;
    private final CraftingEquipUtil craftingEquipUtil = new CraftingEquipUtil();
    private final MainMenuUtil mainMenuUtil = new MainMenuUtil();

    public CraftingEquipInv(Player player) {
        craftingInv = Bukkit.createInventory(player, 54, player.getName() + "'s crafting equipment");
    }

    //private static final int[] editSlots = {19,21,22,23,24,25,25, 28,30,31,32,33,34,34, 37,39,40,41,42,43,43};

    public void setCraftingMenu(Player player) { // 12=baseItemStack, 13=필요 킬포인트, 20~26 29~35 38~44 editVariable , 14=예상 장비 스펙
        setInfo(player);
        String tag = "MPI-CraftingEquipment";
        ItemStack itemStack;
        CraftingInfo craftingInfo = new CraftingInfo(player);
        craftingInv.setItem(12, craftingInfo.getBaseItemStack());
        itemStack = CraftingEquipUtil.getExpectedItemStack(player);
        if (itemStack != null) {
            craftingInv.setItem(14, itemStack);
        }
        List<String> list = new ArrayList<>();
        list.add(ChatColor.GREEN + "투자 가능한 최대 킬 포인트: " + ChatColor.GRAY + craftingInfo.getMaxInvestKillPoint());
        itemStack = createNormalItem(Material.STONE, ChatColor.WHITE + "장비 제작 시작", list, tag, "Start");
        craftingInv.setItem(16, itemStack);
        if (!list.isEmpty()) { list.clear(); }
        list.add(ChatColor.WHITE + "현재 값: " + ChatColor.GRAY + craftingInfo.getTotalKillPoint());
        itemStack = createNormalItem(Material.PAPER, ChatColor.WHITE + "필요한 킬 포인트", list, tag, "requiredKillPoint");
        craftingInv.setItem(13, itemStack);
        if (!list.isEmpty()) { list.clear(); }
        list.add(ChatColor.YELLOW + "좌 클릭: 값 변경");
        list.add(ChatColor.GREEN + "현재 값: " + ChatColor.GRAY + craftingInfo.getEquipmentType().name());
        itemStack = createNormalItem(Material.SMITHING_TABLE, ChatColor.WHITE + "장비 유형", list, tag, "equipmentType");
        craftingInv.setItem(19, itemStack);
        if (!list.isEmpty()) { list.clear(); }
        list.add(ChatColor.YELLOW + "좌 클릭: 값 변경");
        list.add(ChatColor.GREEN + "현재 값: " + ChatColor.GRAY + craftingInfo.isApplySpecialAbility());
        list.add("");
        list.add(ChatColor.GREEN + "50%의 확률로 특수 능력 부여");
        itemStack = createNormalItem(Material.BLACK_BANNER, ChatColor.WHITE + "특수 능력 부여 ("+CraftingEquipUtil.requiredSpecialAbility()+")" , list, tag, "applySpecialAbility");
        craftingInv.setItem(20, itemStack);
        if (!list.isEmpty()) { list.clear(); }
        list.add(ChatColor.YELLOW + "좌 클릭: 1 추가");
        list.add(ChatColor.YELLOW + "우 클릭: 1 감소");
        list.add(ChatColor.WHITE + "현재 값: " + ChatColor.GRAY + craftingInfo.getAddMeleeDamage());
        list.add("");
        list.add(ChatColor.GREEN + "근접 데미지 증가, " + ChatColor.RED + " 공격 속도 감소");
        itemStack = createNormalItem(Material.DIAMOND_SWORD, ChatColor.WHITE + "추가 근접 데미지 ("+CraftingEquipUtil.requiredMeleeDamage()+")", list, tag, "addMeleeDamage");
        craftingInv.setItem(28, itemStack);
        if (!list.isEmpty()) { list.clear(); }
        list.add(ChatColor.YELLOW + "좌 클릭: 1 추가");
        list.add(ChatColor.YELLOW + "우 클릭: 1 감소");
        list.add(ChatColor.WHITE + "현재 값: " + ChatColor.GRAY + craftingInfo.getAddProjectileDamage());
        list.add("");
        list.add(ChatColor.GREEN + "발사체 데미지 증가, " + ChatColor.RED + " 방어력 감소");
        itemStack = createNormalItem(Material.ARROW, ChatColor.WHITE + "추가 발사체 데미지 ("+CraftingEquipUtil.requiredProjectileDamage()+")", list, tag, "addProjectileDamage");
        craftingInv.setItem(29, itemStack);
        if (!list.isEmpty()) { list.clear(); }
        list.add(ChatColor.YELLOW + "좌 클릭: 1 추가");
        list.add(ChatColor.YELLOW + "우 클릭: 1 감소");
        list.add(ChatColor.WHITE + "현재 값: " + ChatColor.GRAY + craftingInfo.getAddAttackSpeed());
        list.add("");
        list.add(ChatColor.GREEN + "공격 속도 증가, " + ChatColor.RED + " 근접 데미지 감소");
        itemStack = createNormalItem(Material.POTION, ChatColor.WHITE + "추가 공격 속도 ("+CraftingEquipUtil.requiredAttackSpeed()+")", list, tag, "addAttackSpeed");
        craftingInv.setItem(30, itemStack);
        if (!list.isEmpty()) { list.clear(); }
        list.add(ChatColor.YELLOW + "좌 클릭: 1 추가");
        list.add(ChatColor.YELLOW + "우 클릭: 1 감소");
        list.add(ChatColor.WHITE + "현재 값: " + ChatColor.GRAY + craftingInfo.getAddDefense());
        list.add("");
        list.add(ChatColor.GREEN + "방어력 증가, " + ChatColor.RED + " 근접 데미지/원거리 데미지/최대체력 중 랜덤 감소");
        list.add("");
        list.add(ChatColor.GREEN + "방어력에 대해(계산식: " + CraftingEquipEvent.getDefenseEquations() + ")");
        list.add(ChatColor.GREEN + "DA = 받은피해, DE = 방어력");
        list.add(ChatColor.GREEN + "");
        list.add(ChatColor.GREEN + "피해의 1/3을 방지하기위해서는 2.5배의 방어력 필요");
        list.add(ChatColor.GREEN + "피해의 1/2을 방지하기위해서는 5배의 방어력 필요");
        list.add(ChatColor.GREEN + "피해의 2/3을 방지하기위해서는 10배의 방어력 필요");
        list.add(ChatColor.GREEN + "피해의 3/4을 방지하기위해서는 15배의 방어력 필요");
        list.add(ChatColor.GREEN + "피해의 90%을 방지하기위해서는 45배의 방어력 필요");
        itemStack = createNormalItem(Material.SHIELD, ChatColor.WHITE + "추가 방어력 ("+CraftingEquipUtil.requiredDefense()+")", list, tag, "addDefense");
        craftingInv.setItem(31, itemStack);
        if (!list.isEmpty()) { list.clear(); }
        list.add(ChatColor.YELLOW + "좌 클릭: 1 추가");
        list.add(ChatColor.YELLOW + "우 클릭: 1 감소");
        list.add(ChatColor.WHITE + "현재 값: " + ChatColor.GRAY + craftingInfo.getAddMaxHealth());
        list.add("");
        list.add(ChatColor.GREEN + "체력 증가, " + ChatColor.RED + " 근접 데미지/원거리 데미지/방어력 중 랜덤 감소");
        itemStack = createNormalItem(Material.GOLDEN_APPLE, ChatColor.WHITE + "추가 체력 ("+CraftingEquipUtil.requiredMaxHealth()+")", list, tag, "addMaxHealth");
        craftingInv.setItem(32, itemStack);
        if (!list.isEmpty()) { list.clear(); }
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
        craftingInv.setItem(4, itemStack);
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
        return craftingInv;
    }
}
