package me.msicraft.modpackintegrated.KillPoint.Task;

import me.msicraft.modpackintegrated.KillPoint.KillPointUtil;
import me.msicraft.modpackintegrated.ModPackIntegrated;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class KillPointTask extends BukkitRunnable {

    private Player player;
    public static int requiredKillPointExp = 999999;

    public KillPointTask(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        if (player.isOnline()) {
            if (requiredKillPointExp != -1) {
                double getKillPointExp = KillPointUtil.getKillPointExp(player);
                if (getKillPointExp >= requiredKillPointExp) {
                    int restValue = (int) (getKillPointExp / requiredKillPointExp);
                    double cal = getKillPointExp - (requiredKillPointExp * restValue);
                    KillPointUtil.addKillPoint(player, restValue);
                    KillPointUtil.setKillPointExp(player, cal);
                }
            }
        } else {
            if (Bukkit.getServer().getScheduler().isCurrentlyRunning(getTaskId())) {
                if (ModPackIntegrated.isDebugEnabled) {
                    Bukkit.getConsoleSender().sendMessage("킬 포인트 스케쥴러 취소: " + player.getName() + " | TaskId: " + getTaskId());
                }
                cancel();
            }
        }
    }
}
