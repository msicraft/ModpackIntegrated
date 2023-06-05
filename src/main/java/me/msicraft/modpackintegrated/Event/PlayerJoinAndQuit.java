package me.msicraft.modpackintegrated.Event;

import me.msicraft.modpackintegrated.CraftingEquip.Enum.SpecialAbility;
import me.msicraft.modpackintegrated.CraftingEquip.Event.CraftingEquipEvent;
import me.msicraft.modpackintegrated.CraftingEquip.Task.CraftingEquipStatTask;
import me.msicraft.modpackintegrated.CraftingEquip.Util.CraftingEquipStatUtil;
import me.msicraft.modpackintegrated.CraftingEquip.Util.CraftingEquipUtil;
import me.msicraft.modpackintegrated.CraftingEquip.Util.SpecialAbilityInfo;
import me.msicraft.modpackintegrated.KillPoint.KillPointUtil;
import me.msicraft.modpackintegrated.ModPackIntegrated;
import me.msicraft.modpackintegrated.PlayerData.File.PlayerDataFile;
import me.msicraft.modpackintegrated.PlayerData.PlayerDataUtil;
import me.msicraft.modpackintegrated.PlayerTask.PlayerTimerTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

public class PlayerJoinAndQuit implements Listener {

    private final PlayerDataUtil playerDataUtil = new PlayerDataUtil();
    private final KillPointUtil killPointUtil = new KillPointUtil();
    private final CraftingEquipStatUtil craftingEquipStatUtil = new CraftingEquipStatUtil();

    private final ItemStack airStack = new ItemStack(Material.AIR, 1);

    public static void updateInventory(Player player) {
        ItemStack[] itemStacks = player.getInventory().getContents();
        for (ItemStack itemStack : itemStacks) {
            if (itemStack != null && itemStack.getType() != Material.AIR) {
                if (CraftingEquipUtil.isCraftingEquipment(itemStack)) {
                    SpecialAbility specialAbility = CraftingEquipStatUtil.getSpecialAbility(itemStack);
                    if (specialAbility != null) {
                        if (SpecialAbilityInfo.hasPercent(specialAbility)) {
                            if (SpecialAbilityInfo.getMaxPercent(specialAbility) < SpecialAbilityInfo.getPercent(itemStack)) {
                                SpecialAbilityInfo.applyItemStackToSpecialAbility(itemStack, specialAbility);
                                CraftingEquipStatUtil.updateAbilityLore(itemStack);
                                continue;
                            }
                        }
                        if (SpecialAbilityInfo.hasMultiValue(specialAbility)) {
                            if (SpecialAbilityInfo.getMaxValue_1(specialAbility) < SpecialAbilityInfo.getValue_1(itemStack)) {
                                SpecialAbilityInfo.applyItemStackToSpecialAbility(itemStack, specialAbility);
                                CraftingEquipStatUtil.updateAbilityLore(itemStack);
                                continue;
                            }
                            if (SpecialAbilityInfo.getMaxValue_2(specialAbility) < SpecialAbilityInfo.getValue_2(itemStack)) {
                                SpecialAbilityInfo.applyItemStackToSpecialAbility(itemStack, specialAbility);
                                CraftingEquipStatUtil.updateAbilityLore(itemStack);
                                continue;
                            }
                        } else {
                            if (SpecialAbilityInfo.getMaxValue(specialAbility) < SpecialAbilityInfo.getValue(itemStack)) {
                                SpecialAbilityInfo.applyItemStackToSpecialAbility(itemStack, specialAbility);
                                CraftingEquipStatUtil.updateAbilityLore(itemStack);
                                continue;
                            }
                        }
                        CraftingEquipStatUtil.updateAbilityLore(itemStack);
                    }
                }
            }
        }
    }

    public static void registerTimerTask(Player player) {
        BukkitTask timerTask = new PlayerTimerTask(player).runTaskTimer(ModPackIntegrated.getPlugin(), 20L, 100L);
        PlayerRelated.addActiveTasks(player, timerTask.getTaskId());
        if (ModPackIntegrated.isDebugEnabled) {
            Bukkit.getConsoleSender().sendMessage("타이머 스케쥴러 등록: " + player.getName() + " | TaskId: " + timerTask.getTaskId());
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        PlayerDataFile playerDataFile = new PlayerDataFile(player);
        if (player.isOnline()) {
            if (!playerDataFile.hasConfigFile()) {
                playerDataUtil.createAndRegisterFile(player);
                if (ModPackIntegrated.isDebugEnabled) {
                    Bukkit.getConsoleSender().sendMessage("플레이어 데이터 생성: " + player.getName());
                }
            }
            killPointUtil.loadKillPoint(player);
            killPointUtil.registerKillPointTask(player);
            craftingEquipStatUtil.registerStatMapTask(player);
            craftingEquipStatUtil.registerStatMap(player);
            registerTimerTask(player);
            Bukkit.getScheduler().runTaskLater(ModPackIntegrated.getPlugin(), ()-> {
                if (!playerDataFile.hasConfigFile()) {
                    player.kickPlayer(ChatColor.RED + "플레이어 데이터가 생성되지 않았습니다.");
                }
            }, 20L);
            if (!ModPackIntegrated.exportEnchantMap.containsKey(player.getUniqueId())) {
                ModPackIntegrated.exportEnchantMap.put(player.getUniqueId(), airStack);
            }
            updateInventory(player);
            if (player.isInvulnerable()) {
                player.setInvulnerable(false);
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        killPointUtil.saveKillPoint(player);
        killPointUtil.removeKillPointMap(player);
        craftingEquipStatUtil.removeStatMap(player);
        PlayerRelated.removeLastDeathLocationMap(player);
        ModPackIntegrated.exportEnchantMap.remove(player.getUniqueId());
        CraftingEquipEvent.removeAbilityMap(player);
        CraftingEquipStatTask.removeMap(player);
        if (player.isInvulnerable()) {
            player.setInvulnerable(false);
        }
    }

}
