package me.msicraft.modpackintegrated.Event.Task;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

public class EnderDragonExpTask extends BukkitRunnable {

    private LivingEntity livingEntity;
    private long endTime;
    private Location location;

    public EnderDragonExpTask(LivingEntity livingEntity) {
        this.livingEntity = livingEntity;
        location = new Location(livingEntity.getWorld(), 0, 85, 0);
        endTime = System.currentTimeMillis() + (40 * 1000);
    }

    @Override
    public void run() {
        long time = System.currentTimeMillis();
        if (endTime > time) {
            World world = livingEntity.getWorld();
            for (Entity entity : world.getNearbyEntities(location, 15, 25, 15)) {
                if (entity instanceof ExperienceOrb experienceOrb) {
                    int exp = experienceOrb.getExperience();
                    if (exp > 250) {
                        experienceOrb.setExperience(250);
                    }
                }
            }
        } else {
            cancel();
        }
    }
}
