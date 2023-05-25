package me.msicraft.modpackintegrated.Event;

import me.msicraft.modpackintegrated.ModPackIntegrated;
import me.msicraft.modpackintegrated.Util.ExpUtil;
import me.msicraft.modpackintegrated.Version.Version_1_16_R3;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class PlayerRelated implements Listener {

    private static final Map<UUID, List<Integer>> playerTasks = new HashMap<>();

    public static boolean containActiveTaskMap(Player player) { return playerTasks.containsKey(player.getUniqueId()); }
    private static List<Integer> getActiveTasks(Player player) { return playerTasks.get(player.getUniqueId()); }
    public static void removeActiveTaskMap(Player player) { playerTasks.remove(player.getUniqueId()); }

    public static void addActiveTasks(Player player, int taskId) {
        List<Integer> tasks = new ArrayList<>();
        if (containActiveTaskMap(player)) {
            tasks.addAll(getActiveTasks(player));
        }
        tasks.add(taskId);
        playerTasks.put(player.getUniqueId(), tasks);
    }

    public static void reloadVariables() {
        isEnabledReducePlayerArrowDamage = ModPackIntegrated.getPlugin().getConfig().contains("Setting.Reduce-ArrowDamage.Enabled") && ModPackIntegrated.getPlugin().getConfig().getBoolean("Setting.Reduce-ArrowDamage.Enabled");
        reduceArrowDamagePercent = ModPackIntegrated.getPlugin().getConfig().contains("Setting.Reduce-ArrowDamage.Percent") ? ModPackIntegrated.getPlugin().getConfig().getDouble("Setting.Reduce-ArrowDamage.Percent") : 1;
        isEnabledRespawnKeepState = ModPackIntegrated.getPlugin().getConfig().contains("Setting.Respawn-KeepState.Enabled") && ModPackIntegrated.getPlugin().getConfig().getBoolean("Setting.Respawn-KeepState.Enabled");
        minHealth = ModPackIntegrated.getPlugin().getConfig().contains("Setting.Respawn-KeepState.Min-Health") ? ModPackIntegrated.getPlugin().getConfig().getDouble("Setting.Respawn-KeepState.Min-Health") : 6;
        isEnabledDisplayItem = ModPackIntegrated.getPlugin().getConfig().contains("Setting.DisplayItem.Enabled") && ModPackIntegrated.getPlugin().getConfig().getBoolean("Setting.DisplayItem.Enabled");
        displayCoolDown = ModPackIntegrated.getPlugin().getConfig().contains("Setting.DisplayItem.Cooldown") ? ModPackIntegrated.getPlugin().getConfig().getDouble("Setting.DisplayItem.Cooldown") : 5;
        isEnabledShowDeathLocation = ModPackIntegrated.getPlugin().getConfig().contains("Setting.ShowDeathLocation.Enabled") && ModPackIntegrated.getPlugin().getConfig().getBoolean("Setting.ShowDeathLocation.Enabled");
        isEnabledMendingEnchant = ModPackIntegrated.getPlugin().getConfig().contains("Setting.MendingEnchant.Enabled") && ModPackIntegrated.getPlugin().getConfig().getBoolean("Setting.MendingEnchant.Enabled");
        mendingEnchantChance = ModPackIntegrated.getPlugin().getConfig().contains("Setting.MendingEnchant.Chance") ? ModPackIntegrated.getPlugin().getConfig().getDouble("Setting.MendingEnchant.Chance") : 0;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        if (containActiveTaskMap(player)) {
            List<Integer> tasks = getActiveTasks(player);
            for (Integer taskId : tasks) {
                Bukkit.getScheduler().cancelTask(taskId);
                if (ModPackIntegrated.isDebugEnabled) {
                    Bukkit.getConsoleSender().sendMessage("플레이어의 스케쥴러 작업 취소됨: " + player.getName() + " | TaskId: " + taskId);
                }
            }
        }
        removeActiveTaskMap(player);
    }

    private static boolean isEnabledReducePlayerArrowDamage = false;
    private static double reduceArrowDamagePercent = 1;
    @EventHandler
    public void onPlayerArrowDamage(EntityDamageByEntityEvent e) {
        if (isEnabledReducePlayerArrowDamage) {
            Entity damager = e.getDamager();
            if (damager instanceof Arrow arrow) {
                if (arrow.getShooter() instanceof Player player) {
                    double damage = e.getDamage();
                    double cal = damage * reduceArrowDamagePercent;
                    e.setDamage(cal);
                    if (ModPackIntegrated.isDebugEnabled) {
                        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "===================================");
                        Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Player arrow damage reduce event");
                        Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Player: " + ChatColor.GREEN + player.getName());
                        Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Original Damage: " + ChatColor.GREEN + damage + ChatColor.GRAY + " | Reduce Percent: " + ChatColor.GREEN + reduceArrowDamagePercent);
                        Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Cal Damage: " + ChatColor.GREEN + cal);
                        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "===================================");
                    }
                }
            }
        }
    }

    private final Map<UUID, double[]> lastDeathInfoMap = new HashMap<>();

    private static boolean isEnabledRespawnKeepState = false;
    private static double minHealth = 6;

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        if (isEnabledRespawnKeepState) {
            Player player = e.getEntity();
            if (player.getGameMode() != GameMode.CREATIVE) { //double[] : [0] = Food Level, [1] = Saturation [2] = exp
                double[] temp = new double[3];
                temp[0] = player.getFoodLevel();
                temp[1] = player.getSaturation();
                temp[2] = ExpUtil.getPlayerExp(player);
                lastDeathInfoMap.put(player.getUniqueId(), temp);
                if (ModPackIntegrated.isDebugEnabled) {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "===================================");
                    Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Player death data save event");
                    Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Player: " + ChatColor.GREEN + player.getName());
                    Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "FoodLevel: " + ChatColor.GREEN + temp[0]);
                    Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Saturation: " + ChatColor.GREEN + temp[1]);
                    Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "===================================");
                }
            }
        }
    }

    @EventHandler
    public void onRespawnPlayer(PlayerRespawnEvent e) {
        Player player = e.getPlayer();
        if (isEnabledRespawnKeepState) {
            if (player.getGameMode() != GameMode.CREATIVE && lastDeathInfoMap.containsKey(player.getUniqueId())) {
                double[] temp = lastDeathInfoMap.get(player.getUniqueId());
                double lastFoodLevel = temp[0];
                double lastSaturation = temp[1];
                double lastExp = temp[2];
                int expCal = (int) Math.round(lastExp*0.5);
                Bukkit.getScheduler().runTask(ModPackIntegrated.getPlugin(), ()-> {
                    player.setHealth(minHealth);
                    player.setFoodLevel((int) lastFoodLevel);
                    player.setSaturation((float) lastSaturation);
                    ExpUtil.changePlayerExp(player, expCal);
                    lastDeathInfoMap.remove(player.getUniqueId());
                    if (ModPackIntegrated.isDebugEnabled) {
                        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "===================================");
                        Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Player respawn keep state event");
                        Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Player: " + ChatColor.GREEN + player.getName());
                        Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Health: " + ChatColor.GREEN + minHealth);
                        Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "FoodLevel: " + ChatColor.GREEN + lastFoodLevel);
                        Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "Saturation: " + ChatColor.GREEN + lastSaturation);
                        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "===================================");
                    }
                });
            }
        }
    }

    public final Map<UUID, Long> displayItemCooldownMap = new HashMap<>();
    private static boolean isEnabledDisplayItem = false;
    private static double displayCoolDown = 5;

    @EventHandler
    public void onInventoryItemDisplayLink(InventoryClickEvent e) {
        if (isEnabledDisplayItem) {
            if (e.getClickedInventory() == null) { return; }
            if (e.getClickedInventory().getType() == InventoryType.PLAYER) {
                if (e.getView().getTitle().equals("container.crafting") || e.getView().getTitle().equals("Crafting")) {
                    if (e.getCurrentItem() == null) {
                        return;
                    }
                    ClickType clickType = e.getClick();
                    if (clickType == ClickType.SHIFT_RIGHT) {
                        ItemStack itemStack = e.getCurrentItem();
                        if (itemStack.getType() == Material.AIR) {
                            return;
                        }
                        e.setCancelled(true);
                        Player player = (Player) e.getWhoClicked();
                        if (displayItemCooldownMap.containsKey(player.getUniqueId())) {
                            if (displayItemCooldownMap.get(player.getUniqueId()) > System.currentTimeMillis()) {
                                long timeLeft = (displayItemCooldownMap.get(player.getUniqueId()) - System.currentTimeMillis()) / 1000;
                                player.sendMessage(ChatColor.RED + "쿨타임 입니다. " + ChatColor.GRAY + timeLeft);
                                return;
                            }
                        }
                        long cooldown = (long) (System.currentTimeMillis() + (displayCoolDown * 1000L));
                        displayItemCooldownMap.put(player.getUniqueId(), cooldown);
                        ItemStack temp = new ItemStack(itemStack);
                        ItemMeta itemMeta = temp.getItemMeta();
                        List<String> loreList = new ArrayList<>();
                        String itemName;
                        int amount = temp.getAmount();
                        if (itemMeta != null && itemMeta.hasDisplayName()) {
                            itemName = itemMeta.getDisplayName();
                        } else if (itemMeta != null && itemMeta.hasLocalizedName()) {
                            itemName = itemMeta.getLocalizedName();
                        } else {
                            itemName = temp.getType().name().toUpperCase();
                        }
                        if (itemMeta != null && itemMeta.hasLore()) {
                            loreList = itemMeta.getLore();
                        }
                        StringBuilder tempS = new StringBuilder();
                        tempS.append(itemName).append("\n");
                        Map<Enchantment, Integer> enchantMap = temp.getEnchantments();
                        for (Enchantment enchantment : enchantMap.keySet()) {
                            tempS.append(ChatColor.GRAY).append(enchantment.getName()).append(" ").append(enchantMap.get(enchantment)).append("\n");
                        }
                        if (loreList != null && !loreList.isEmpty()) {
                            for (String item : loreList) {
                                tempS.append(item).append("\n");
                            }
                        }
                        TextComponent main = new TextComponent(ChatColor.WHITE + player.getName() + ": ");
                        main.setColor(net.md_5.bungee.api.ChatColor.WHITE);
                        TextComponent sub = new TextComponent("[" + ChatColor.AQUA + itemName + " X " + amount + ChatColor.WHITE + "]");
                        HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(tempS.toString()).create());
                        if (ModPackIntegrated.bukkitVersion.equals("1.16.5")) {
                            hoverEvent = Version_1_16_R3.getHoverEventByShowItem(temp);
                        }
                        sub.setHoverEvent(hoverEvent);
                        main.addExtra(sub);
                        Bukkit.spigot().broadcast(main);
                    }
                }
            }
        }
    }

    private static boolean isEnabledShowDeathLocation = false;
    private static final Map<UUID, Location> lastDeathLocation = new HashMap<>();

    public static boolean hasLastDeathLocation(Player player) {
        boolean check = false;
        if (lastDeathLocation.containsKey(player.getUniqueId())) {
            if (lastDeathLocation.get(player.getUniqueId()) != null) {
                check = true;
            }
        }
        return check;
    }

    public static Location getDeathLocation(Player player) { return lastDeathLocation.get(player.getUniqueId()); }

    public static void removeLastDeathLocationMap(Player player) { lastDeathLocation.remove(player.getUniqueId()); }

    @EventHandler
    public void onShowDeathLocationSave(PlayerDeathEvent e) {
        if (isEnabledShowDeathLocation) {
            Player player = e.getEntity();
            Location deathLocation = player.getLocation();
            lastDeathLocation.put(player.getUniqueId(), deathLocation);
        }
    }

    @EventHandler
    public void onShowDeathLocation(PlayerRespawnEvent e) {
        if (isEnabledShowDeathLocation) {
            Player player = e.getPlayer();
            if (lastDeathLocation.containsKey(player.getUniqueId())) {
                Bukkit.getScheduler().runTask(ModPackIntegrated.getPlugin(), ()-> {
                    Location location = lastDeathLocation.get(player.getUniqueId());
                    player.sendMessage(ChatColor.YELLOW + "========================================");
                    player.sendMessage(ChatColor.GREEN + "마지막으로 죽은 위치");
                    player.sendMessage(ChatColor.GRAY + "월드: " + ChatColor.WHITE + location.getWorld().getName());
                    player.sendMessage(ChatColor.GRAY + "X: " + ChatColor.WHITE + location.getBlockX());
                    player.sendMessage(ChatColor.GRAY + "Y: " + ChatColor.WHITE + location.getBlockY());
                    player.sendMessage(ChatColor.GRAY + "Z: " + ChatColor.WHITE + location.getBlockZ());
                    player.sendMessage(ChatColor.YELLOW + "========================================");
                });
            }
        }
    }

    private static boolean isEnabledMendingEnchant = false;
    private static double mendingEnchantChance = 0;

    @EventHandler
    public void mendingEnchant(EnchantItemEvent e) {
        if (isEnabledMendingEnchant) {
            double randomChance = Math.random();
            boolean isSuccess = false;
            if (e.getExpLevelCost() >= 30) {
                if (randomChance < mendingEnchantChance) {
                    e.getEnchantsToAdd().put(Enchantment.MENDING, 1);
                    isSuccess = true;
                }
            }
            if (ModPackIntegrated.isDebugEnabled) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "========================================");
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "아이템 인챈트 시도");
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "플레이어: " + ChatColor.WHITE + e.getEnchanter().getName());
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "랜덤 확률: " + ChatColor.WHITE + randomChance);
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "확률: " + ChatColor.WHITE + mendingEnchantChance);
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "성공 여부: " + ChatColor.WHITE + isSuccess);
                Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "========================================");
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void attackCoolDown(EntityDamageByEntityEvent e) {
        Entity damager = e.getDamager();
        if (damager instanceof Player player) {
            ItemStack handItem = player.getInventory().getItemInMainHand();
            if (handItem != null && handItem.getType() != Material.AIR) {
                AttributeInstance instance = player.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
                if (instance != null) {
                    Material material = handItem.getType();
                    if (player.hasCooldown(material)) {
                        e.setCancelled(true);
                        return;
                    }
                    double attackSpeed = instance.getValue();
                    double calCD = 1/attackSpeed;
                    int calTick = (int) Math.round(20 * calCD);
                    player.setCooldown(material, calTick);
                }
            }
        }
    }

}
