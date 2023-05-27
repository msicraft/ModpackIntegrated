package me.msicraft.modpackintegrated.CraftingEquip.Doppelganger;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;

import java.util.Arrays;
import java.util.List;

public class DoppelgangerTask extends BukkitRunnable {

    private LivingEntity livingEntity;
    private final double distance = 4.1;

    public DoppelgangerTask(LivingEntity livingEntity) {
        this.livingEntity = livingEntity;
    }

    private final List<Material> banBlockTypes = Arrays.asList(Material.AIR, Material.BEDROCK, Material.OBSIDIAN);

    @Override
    public void run() {
        if (!livingEntity.isDead() && livingEntity.getHealth() > 0) {
            RayTraceResult rayTraceResult = livingEntity.rayTraceBlocks(distance);
            if (rayTraceResult != null) {
                Block block = rayTraceResult.getHitBlock();
                if (block != null && !banBlockTypes.contains(block.getType())) {
                    block.breakNaturally();
                }
            }
            Location eyeLocation = livingEntity.getEyeLocation();
            eyeLocation.setY(eyeLocation.getY() - 0.2);
            RayTraceResult rayTraceResult1 = livingEntity.getWorld().rayTraceEntities(livingEntity.getLocation(), eyeLocation.getDirection(), distance, 1.25, p -> p.getType() == EntityType.PLAYER && !p.isDead());
            if (rayTraceResult1 != null) {
                Entity entity = rayTraceResult1.getHitEntity();
                if (entity != null) {
                    if (entity instanceof Player player) {
                        if (Math.random() < 0.6) {
                            double attackDamage = getAttackDamage(livingEntity);
                            livingEntity.swingMainHand();
                            player.damage(attackDamage, livingEntity);
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
