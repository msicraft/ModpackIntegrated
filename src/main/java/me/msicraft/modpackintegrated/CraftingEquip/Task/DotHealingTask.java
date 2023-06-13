package me.msicraft.modpackintegrated.CraftingEquip.Task;

import me.msicraft.modpackintegrated.CraftingEquip.Util.SpecialAbilityUtil;
import me.msicraft.modpackintegrated.ModPackIntegrated;
import me.msicraft.modpackintegrated.PlayerData.File.PlayerDataFile;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class DotHealingTask extends BukkitRunnable {

    private LivingEntity livingEntity;
    private int maxTicks;
    private double perHealAmount;
    private int tickCount = 0;
    private PlayerDataFile dataFile = null;

    public DotHealingTask(LivingEntity livingEntity, int maxTicks, double totalHealing) {
        this.livingEntity = livingEntity;
        this.maxTicks = maxTicks;
        perHealAmount = (totalHealing / (maxTicks/10.0));
        if (livingEntity instanceof Player player) {
            dataFile = new PlayerDataFile(player);
        }
    }

    @Override
    public void run() {
        if (!livingEntity.isDead() && livingEntity.getHealth() > 0) {
            if (livingEntity instanceof Player player) {
                SpecialAbilityUtil.healPlayer(player, perHealAmount);
            }
            tickCount = tickCount + 10;
            Bukkit.getScheduler().runTask(ModPackIntegrated.getPlugin(), ()-> {
                if (livingEntity.getHealth() <= 0 || livingEntity.isDead()) {
                    cancel();
                }
            });
        } else {
            cancel();
        }
    }
}
