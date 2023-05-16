package me.msicraft.modpackintegrated.PlayerTask;

import me.msicraft.modpackintegrated.ModPackIntegrated;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerTimerTask extends BukkitRunnable {

    private Player player;
    private final double percent = 0.05;

    public PlayerTimerTask(Player player) {
        this.player = player;
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
            if (!player.hasPotionEffect(PotionEffectType.GLOWING)) {
                PotionEffect glowingEffect = new PotionEffect(PotionEffectType.GLOWING, 999999, 255, false, false);
                player.addPotionEffect(glowingEffect);
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
