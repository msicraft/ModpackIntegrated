package me.msicraft.modpackintegrated.PlayerTask;

import me.msicraft.modpackintegrated.ModPackIntegrated;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerTimerTask extends BukkitRunnable {

    private Player player;
    private final double percent = 0.05;
    private boolean isEnabledKeepGlowingEffect;

    public PlayerTimerTask(Player player) {
        this.player = player;
        isEnabledKeepGlowingEffect = ModPackIntegrated.getPlugin().getConfig().contains("Setting.KeepGlowingEffect.Enabled") && ModPackIntegrated.getPlugin().getConfig().getBoolean("Setting.KeepGlowingEffect.Enabled");
    }

    @Override
    public void run() {
        if (player.isOnline()) {
            double maxHealth = player.getMaxHealth();
            double percentCal = maxHealth * percent;
            double cal = player.getHealth() + percentCal;
            if (cal > player.getMaxHealth()) {
                cal = player.getMaxHealth();
            }
            player.setHealth(cal);
            if (isEnabledKeepGlowingEffect) {
                if (!player.isGlowing()) {
                    player.setGlowing(true);
                }
            }
        } else {
            if (Bukkit.getServer().getScheduler().isCurrentlyRunning(getTaskId())) {
                if (ModPackIntegrated.isDebugEnabled) {
                    Bukkit.getConsoleSender().sendMessage("스케쥴러 취소: " + player.getName() + " | TaskId: " + getTaskId());
                }
                cancel();
            }
        }
    }
}
