package me.msicraft.modpackintegrated.EntityBlockBreak.Task;

import me.msicraft.modpackintegrated.EntityBlockBreak.EntityBlockBreakUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EntityBlockBreakTask extends BukkitRunnable {

    private LivingEntity livingEntity;
    private final double chance = 0.1;

    public EntityBlockBreakTask(LivingEntity livingEntity) {
        this.livingEntity = livingEntity;
    }

    private final List<Material> banBlockTypes = new ArrayList<>(Arrays.asList(Material.AIR, Material.BEDROCK, Material.OBSIDIAN));

    @Override
    public void run() {
        if (!livingEntity.isDead()) {
            if (livingEntity.getHealth() > 0) {
                if (Math.random() < chance) {
                    if (EntityBlockBreakUtil.hasBlockBreakTag(livingEntity)) {
                        boolean hasNearPlayer = false;
                        for (Entity entity : livingEntity.getNearbyEntities(5,5,5)) {
                            if (entity instanceof Player player) {
                                hasNearPlayer = true;
                                break;
                            }
                        }
                        if (hasNearPlayer) {
                            RayTraceResult rayTraceResult = livingEntity.rayTraceBlocks(4);
                            if (rayTraceResult != null) {
                                Block block = rayTraceResult.getHitBlock();
                                if (block != null && !banBlockTypes.contains(block.getType())) {
                                    block.breakNaturally();
                                }
                            }
                        }
                    }
                }
            }
        } else {
            cancel();
        }
    }
}