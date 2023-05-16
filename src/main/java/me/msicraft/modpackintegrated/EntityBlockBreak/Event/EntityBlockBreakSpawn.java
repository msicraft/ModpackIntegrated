package me.msicraft.modpackintegrated.EntityBlockBreak.Event;

import me.msicraft.modpackintegrated.EntityBlockBreak.EntityBlockBreakUtil;
import me.msicraft.modpackintegrated.EntityBlockBreak.Task.EntityBlockBreakTask;
import me.msicraft.modpackintegrated.ModPackIntegrated;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.Random;

public class EntityBlockBreakSpawn implements Listener {

    private final Random random = new Random();

    @EventHandler(priority = EventPriority.LOWEST)
    public void spawn(CreatureSpawnEvent e) {
        if (Math.random() < 0.2) {
            LivingEntity livingEntity = e.getEntity();
            EntityBlockBreakUtil.applyBlockBreakTag(livingEntity);
            int r = random.nextInt(51);
            new EntityBlockBreakTask(livingEntity).runTaskTimer(ModPackIntegrated.getPlugin(), 20L, (100 + r));
        }
    }

}
