package me.msicraft.modpackintegrated.CraftingEquip.Doppelganger;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;

import java.util.Arrays;
import java.util.List;

public class DoppelgangerTask extends BukkitRunnable {

    private LivingEntity livingEntity;
    private final double distance = 4;

    public DoppelgangerTask(LivingEntity livingEntity) {
        this.livingEntity = livingEntity;
    }

    private final List<Material> banBlockTypes = Arrays.asList(Material.AIR, Material.BEDROCK, Material.OBSIDIAN);

    @Override
    public void run() {
        if (!livingEntity.isDead() && livingEntity.getHealth() > 0) {
            if (Math.random() < 0.5) {
                RayTraceResult rayTraceResult = livingEntity.rayTraceBlocks(distance);
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
            Location eyeLocation = livingEntity.getEyeLocation();
            eyeLocation.setY(eyeLocation.getY() - 0.2);
            RayTraceResult rayTraceResult1 = livingEntity.getWorld().rayTraceEntities(livingEntity.getLocation(), eyeLocation.getDirection(), distance, 1.2, p -> p.getType() == EntityType.PLAYER && !p.isDead());
            if (rayTraceResult1 != null) {
                Entity entity = rayTraceResult1.getHitEntity();
                if (entity != null) {
                    if (entity instanceof Player player) {
                        if (Math.random() < 0.1) {
                            ItemStack mainHand = player.getInventory().getItemInMainHand();
                            ItemStack offHand = player.getInventory().getItemInOffHand();
                            if (mainHand != null && mainHand.getType() != Material.AIR) {
                                player.setCooldown(mainHand.getType(), 80);
                            }
                            if (offHand != null && offHand.getType() != Material.AIR) {
                                player.setCooldown(offHand.getType(), 80);
                            }
                        }
                        if (Math.random() < 0.6) {
                            double attackDamage = getAttackDamage(livingEntity);
                            livingEntity.swingMainHand();
                            player.damage(attackDamage, livingEntity);
                        }
                    }
                }
            }
            if (Math.random() < 0.75) {
                if (livingEntity instanceof Mob mob) {
                    for (Entity entity : livingEntity.getNearbyEntities(3, 2, 3)) {
                        if (entity instanceof Player player) {
                            mob.setTarget(player);
                            break;
                        }
                    }
                }
            }
        } else {
            cancel();
        }
    }

    private static double getAttackDamage(LivingEntity livingEntity) {
        double d = 0;
        AttributeInstance instance = livingEntity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
        if (instance != null) {
            d = instance.getValue();
        }
        return d;
    }

}
