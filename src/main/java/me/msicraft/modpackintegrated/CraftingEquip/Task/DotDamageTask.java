package me.msicraft.modpackintegrated.CraftingEquip.Task;

import me.msicraft.modpackintegrated.CraftingEquip.Util.SpecialAbilityUtil;
import me.msicraft.modpackintegrated.Event.PlayerRelated;
import me.msicraft.modpackintegrated.ModPackIntegrated;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class DotDamageTask extends BukkitRunnable {

    private LivingEntity livingEntity;
    private int maxTicks;
    private double perDamage;
    private int tickCount = 0;

    public DotDamageTask(LivingEntity livingEntity, int maxTicks, double totalDamage) {
        this.livingEntity = livingEntity;
        this.maxTicks = maxTicks;
        perDamage = (totalDamage / (maxTicks/10.0));
        if (PlayerRelated.enabledFixFinalDamage()) {
            if (perDamage < 0.5) {
                perDamage = 0.5;
            }
        }
    }

    @Override
    public void run() {
        if (!livingEntity.isDead() && livingEntity.getHealth() > 0) {
            SpecialAbilityUtil.applyTrueDamage(livingEntity, perDamage);
            if (livingEntity instanceof Player player) {
                double cal = Math.round(perDamage * 100.0) /100.0;
                player.sendMessage(ChatColor.GRAY + "도트 데미지 받음: " + ChatColor.RED + cal);
            }
            tickCount = tickCount + 10;
            Bukkit.getScheduler().runTask(ModPackIntegrated.getPlugin(), ()-> {
                livingEntity.setNoDamageTicks(0);
                if (livingEntity.getHealth() <= 0 || livingEntity.isDead()) {
                    cancel();
                }
            });
            if (tickCount > maxTicks) {
                cancel();
            }
        } else {
            cancel();
        }
    }

}
