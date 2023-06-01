package me.msicraft.modpackintegrated.EntityBlockBreak.Task;

import me.msicraft.modpackintegrated.EntityBlockBreak.EntityBlockBreakUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;

import java.util.Arrays;
import java.util.List;

public class EntityBlockBreakTask extends BukkitRunnable {

    private LivingEntity livingEntity;
    private double chance;

    public EntityBlockBreakTask(LivingEntity livingEntity, double chance) {
        this.livingEntity = livingEntity;
        this.chance = chance;
    }

    private final List<Material> banBlockTypes = Arrays.asList(Material.AIR, Material.BEDROCK, Material.OBSIDIAN);

    @Override
    public void run() {
        if (!livingEntity.isDead() && livingEntity.getHealth() > 0) {
            if (Math.random() < chance) {
                if (EntityBlockBreakUtil.hasBlockBreakTag(livingEntity)) {
                    boolean hasNearPlayer = false;
                    for (Entity entity : livingEntity.getNearbyEntities(6,5,6)) {
                        if (entity instanceof Player) {
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
                                Location downLoc = new Location(block.getWorld(), block.getX(), block.getY() - 1, block.getZ());
                                Block downBlock = downLoc.getBlock();
                                if (downBlock != null && !banBlockTypes.contains(downBlock.getType())) {
                                    downBlock.breakNaturally();
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
