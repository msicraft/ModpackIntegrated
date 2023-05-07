package me.msicraft.modpackintegrated.MainMenu.ExportEnchant;

import me.msicraft.modpackintegrated.ModPackIntegrated;
import me.msicraft.modpackintegrated.Util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ExportEnchantUtil {

    private final ItemStack airStack = new ItemStack(Material.AIR, 1);

    public int getEnchantAmount(ItemStack itemStack) { return itemStack.getEnchantments().size(); }

    public void sendPlayerToMap(Player player, int slot, ItemStack itemStack) {
        ItemStack exportItem = ModPackIntegrated.exportEnchantMap.get(player.getUniqueId());
        if (exportItem.getType() == Material.AIR) {
            ModPackIntegrated.exportEnchantMap.put(player.getUniqueId(), itemStack);
            player.getInventory().setItem(slot, airStack);
        } else {
            player.sendMessage(ChatColor.RED + "이미 추출 장비가 존재합니다.");
        }
    }

    public void sendMapToPlayer(Player player, int slot) {
        if (slot == 22) {
            ItemStack exportItem = ModPackIntegrated.exportEnchantMap.get(player.getUniqueId());
            if (exportItem.getType() != Material.AIR) {
                int emptySlot = PlayerUtil.getPlayerEmptySlot(player);
                if (emptySlot != -1) {
                    player.getInventory().addItem(exportItem);
                    ModPackIntegrated.exportEnchantMap.put(player.getUniqueId(), airStack);
                } else {
                    player.sendMessage(ChatColor.RED + "인벤토리에 빈 공간이 없습니다.");
                }
            }
        }
    }

    private final Random random = new Random();

    public boolean startExportEnchant(Player player) {
        boolean isSuccess = false;
        ItemStack exportItem = ModPackIntegrated.exportEnchantMap.get(player.getUniqueId());
        if (exportItem.getType() != Material.AIR) {
            ItemStack book = new ItemStack(Material.ENCHANTED_BOOK, 1);
            EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta) book.getItemMeta();
            if (enchantmentStorageMeta != null) {
                Map<Enchantment, Integer> enchantMap = exportItem.getEnchantments();
                int maxSize = enchantMap.size();
                int randomSelect = random.nextInt(maxSize);
                int count = 0;
                double extraEnchantChance = 0.2;
                Map<Enchantment, Boolean> debugMap = null;
                if (ModPackIntegrated.isDebugEnabled) {
                    debugMap = new HashMap<>();
                }
                for (Enchantment enchantment : enchantMap.keySet()) {
                    boolean deSuccess = false;
                    int level = enchantMap.get(enchantment);
                    if (count == randomSelect) {
                        enchantmentStorageMeta.addStoredEnchant(enchantment, level, true);
                        deSuccess = true;
                    } else {
                        double extraChance = Math.random();
                        if (extraChance < extraEnchantChance) {
                            extraEnchantChance = extraEnchantChance / 2;
                            enchantmentStorageMeta.addStoredEnchant(enchantment, level, true);
                            deSuccess = true;
                        }
                    }
                    if (ModPackIntegrated.isDebugEnabled && debugMap != null) {
                        debugMap.put(enchantment, deSuccess);
                    }
                    count++;
                }
                if (ModPackIntegrated.isDebugEnabled) {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "========================================");
                    Bukkit.getConsoleSender().sendMessage(ChatColor.WHITE + "인챈트 추출 시도");
                    Bukkit.getConsoleSender().sendMessage(ChatColor.WHITE + "플레이어: " + ChatColor.GREEN + player.getName());
                    if (debugMap != null) {
                        for (Enchantment enchantment : debugMap.keySet()) {
                            Bukkit.getConsoleSender().sendMessage(ChatColor.WHITE + "인챈트: " + enchantment.getName() + " | 성공여부: " + debugMap.get(enchantment));
                        }
                    }
                    Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "========================================");
                }
                book.setItemMeta(enchantmentStorageMeta);
                int emptySlot = PlayerUtil.getPlayerEmptySlot(player);
                if (emptySlot != -1) {
                    player.getInventory().addItem(book);
                } else {
                    player.getWorld().dropItemNaturally(player.getLocation(), book);
                }
                ModPackIntegrated.exportEnchantMap.put(player.getUniqueId(), airStack);
                isSuccess = true;
            }
        }
        return isSuccess;
    }

    public boolean startFullExportEnchant(Player player) {
        boolean isSuccess = false;
        ItemStack exportItem = ModPackIntegrated.exportEnchantMap.get(player.getUniqueId());
        if (exportItem.getType() != Material.AIR) {
            ItemStack book = new ItemStack(Material.ENCHANTED_BOOK, 1);
            EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta) book.getItemMeta();
            if (enchantmentStorageMeta != null) {
                Map<Enchantment, Integer> enchantMap = exportItem.getEnchantments();
                for (Enchantment enchantment : enchantMap.keySet()) {
                    enchantmentStorageMeta.addStoredEnchant(enchantment, enchantMap.get(enchantment), true);
                }
                book.setItemMeta(enchantmentStorageMeta);
                int emptySlot = PlayerUtil.getPlayerEmptySlot(player);
                if (emptySlot != -1) {
                    player.getInventory().addItem(book);
                } else {
                    player.getWorld().dropItemNaturally(player.getLocation(), book);
                }
                ModPackIntegrated.exportEnchantMap.put(player.getUniqueId(), airStack);
                isSuccess = true;
            }
        }
        return isSuccess;
    }

}
