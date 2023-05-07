package me.msicraft.modpackintegrated.KillPoint;

import me.msicraft.modpackintegrated.Event.PlayerRelated;
import me.msicraft.modpackintegrated.KillPoint.Task.KillPointTask;
import me.msicraft.modpackintegrated.ModPackIntegrated;
import me.msicraft.modpackintegrated.PlayerData.File.PlayerDataFile;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KillPointUtil {

    public static final Map<UUID, Integer> killPointMap = new HashMap<>();
    private static final Map<UUID, Double> killPointExpMap = new HashMap<>();
    private static String killPointExpEquations = null;
    private static double perEntity_MaxExpValue = 0;
    private static Expression expression = null;
    private static double killPointExpSpread = 0;

    public void reloadVariables() {
        reloadKillPointExp();
        reloadKillPointExpEquations();
    }

    public void registerKillPointTask(Player player) {
        BukkitTask killPointTask = new KillPointTask(player).runTaskTimer(ModPackIntegrated.getPlugin(), 20, 1200);
        PlayerRelated.addActiveTasks(player, killPointTask.getTaskId());
        if (ModPackIntegrated.isDebugEnabled) {
            Bukkit.getConsoleSender().sendMessage("킬 포인트 스케쥴러 등록: " + player.getName() + " | TaskId: " + killPointTask.getTaskId());
        }
    }

    private void reloadKillPointExpEquations() {
        killPointExpEquations = ModPackIntegrated.getPlugin().getConfig().contains("Kill-Point-Setting.KillPointExpEquations") ? ModPackIntegrated.getPlugin().getConfig().getString("Kill-Point-Setting.KillPointExpEquations") : null;
        perEntity_MaxExpValue = ModPackIntegrated.getPlugin().getConfig().contains("Kill-Point-Setting.PerEntity-MaxExpValue") ? ModPackIntegrated.getPlugin().getConfig().getDouble("Kill-Point-Setting.PerEntity-MaxExpValue") : 0;
        if (killPointExpEquations == null) {
            killPointExpEquations = "(H + D + (A * 1.15) + (AT * 1.5))";
        }
        try {
            expression = new ExpressionBuilder(killPointExpEquations).variables("H", "D", "A", "AT").build();
        } catch (IllegalArgumentException e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "잘못된 표현식 발견: " + ChatColor.GREEN + killPointExpEquations);
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "기본 표현식이 사용됩니다. " + " (H + D + (A * 0.5) + (AT * 0.75))");
            killPointExpEquations = "(H + D + (A * 1.15) + (AT * 1.5))";
        }
        killPointExpSpread = ModPackIntegrated.getPlugin().getConfig().contains("Kill-Point-Setting.KillPointExpSpread") ? ModPackIntegrated.getPlugin().getConfig().getDouble("Kill-Point-Setting.KillPointExpSpread") : 0;
    }

    public static String getKillPointExpEquations() {
        return killPointExpEquations;
    }

    private void reloadKillPointExp() {
        if (ModPackIntegrated.getPlugin().getConfig().contains("Kill-Point-Setting.Required-Exp")) {
            KillPointTask.requiredKillPointExp = ModPackIntegrated.getPlugin().getConfig().getInt("Kill-Point-Setting.Required-Exp");
        }
    }

    public void saveAllKillPoint() {
        for (UUID uuid : killPointMap.keySet()) {
            int killPoint = killPointMap.get(uuid);
            double exp = killPointExpMap.get(uuid);
            PlayerDataFile playerDataFile = new PlayerDataFile(uuid);
            playerDataFile.getConfig().set("Kill-Point", killPoint);
            playerDataFile.getConfig().set("Kill-PointExp", exp);
            playerDataFile.saveConfig();
        }
    }

    public void saveKillPoint(Player player) {
        if (killPointMap.containsKey(player.getUniqueId())) {
            int killPoint = killPointMap.get(player.getUniqueId());
            double exp = killPointExpMap.get(player.getUniqueId());
            PlayerDataFile playerDataFile = new PlayerDataFile(player);
            playerDataFile.getConfig().set("Kill-Point", killPoint);
            playerDataFile.getConfig().set("Kill-PointExp", exp);
            playerDataFile.saveConfig();
        }
    }

    public void loadKillPoint(Player player) {
        killPointMap.put(player.getUniqueId(), getFileKillPoint(player));
        killPointExpMap.put(player.getUniqueId(), getFileKillPointExp(player));
    }

    public void removeKillPointMap(Player player) {
        killPointMap.remove(player.getUniqueId());
        killPointExpMap.remove(player.getUniqueId());
    }

    private int getFileKillPoint(Player player) {
        int value = 0;
        PlayerDataFile playerDataFile = new PlayerDataFile(player);
        if (playerDataFile.getConfig().contains("Kill-Point")) {
            value = playerDataFile.getConfig().getInt("Kill-Point");
        }
        return value;
    }

    private double getFileKillPointExp(Player player) {
        double value = 0;
        PlayerDataFile playerDataFile = new PlayerDataFile(player);
        if (playerDataFile.getConfig().contains("Kill-PointExp")) {
            value = playerDataFile.getConfig().getDouble("Kill-PointExp");
        }
        return value;
    }

    public static int getKillPoint(Player player) {
        int value = 0;
        if (killPointMap.containsKey(player.getUniqueId())) {
            value = killPointMap.get(player.getUniqueId());
        }
        return value;
    }

    public static void addKillPoint(Player player, int amount) {
        int cal = getKillPoint(player) + amount;
        killPointMap.put(player.getUniqueId(), cal);
    }

    public static void setKillPoint(Player player, int amount) {
        killPointMap.put(player.getUniqueId(), amount);
    }

    public static double getKillPointExp(Player player) {
        double value = 0;
        if (killPointExpMap.containsKey(player.getUniqueId())) {
            value = killPointExpMap.get(player.getUniqueId());
        }
        return value;
    }

    public static void addKillPointExp(Player player, double value) {
        double cal = getKillPointExp(player) + value;
        killPointExpMap.put(player.getUniqueId(), cal);
    }

    public static void setKillPointExp(Player player, double value) {
        killPointExpMap.put(player.getUniqueId(), value);
    }

    public static double getRandomValue(double max, double min) {
        double randomValue = (Math.random() * (max - min)) + min;
        return (Math.floor(randomValue * 1000) / 1000.0);
    }

    public static double getToEntityKillPointExp(double health, double damage, double armor, double armorToughness) {
        double exp = 0;
        if (killPointExpEquations != null && expression != null) {
            double v;
            if (health > 1200) {
                health = health * 0.5;
            }
            v = expression.setVariable("H", health).setVariable("D", damage).setVariable("A", armor).setVariable("AT", armorToughness).evaluate();
            v = Math.round(v * 1000.0) / 1000.0;
            double spreadV = v * killPointExpSpread;
            double max = v + spreadV;
            double min = v - spreadV;
            if (min < 0) {
                min = 0;
            }
            double randomExp = getRandomValue(max, min);
            if (randomExp >= perEntity_MaxExpValue) {
                randomExp = perEntity_MaxExpValue;
            }
            exp = randomExp;
            if (ModPackIntegrated.isDebugEnabled) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "========================================");
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "랜덤 킬 포인트 경험치 계산");
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "범위: " + killPointExpSpread + "| 최대: " + max + "| 최소: " + min + "| 값: " + exp);
                Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "========================================");
            }
        } else {
            if (ModPackIntegrated.isDebugEnabled) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "========================================");
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "KillPointExpEquations: " + ChatColor.GRAY + killPointExpEquations);
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Expression: " + ChatColor.GRAY + expression);
                Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "========================================");
            }
        }
        return exp;
    }

    public static boolean hasKillPoint(Player player, int requiredPoint) {
        boolean check = false;
        int getKillPoint = getKillPoint(player);
        if (getKillPoint >= requiredPoint) {
            check = true;
        }
        return check;
    }

}
