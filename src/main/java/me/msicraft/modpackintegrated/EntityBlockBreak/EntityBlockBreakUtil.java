package me.msicraft.modpackintegrated.EntityBlockBreak;

import me.msicraft.modpackintegrated.ModPackIntegrated;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class EntityBlockBreakUtil {

    private static String blockBreakTagKey = "MPI-Entity-BlockBreak";

    public static void applyBlockBreakTag(LivingEntity livingEntity) {
        PersistentDataContainer data = livingEntity.getPersistentDataContainer();
        data.set(new NamespacedKey(ModPackIntegrated.getPlugin(), blockBreakTagKey), PersistentDataType.STRING, "BlockBreakTag");
    }

    public static boolean hasBlockBreakTag(LivingEntity livingEntity) {
        boolean check = false;
        PersistentDataContainer data = livingEntity.getPersistentDataContainer();
        if (data.has(new NamespacedKey(ModPackIntegrated.getPlugin(), blockBreakTagKey), PersistentDataType.STRING)) {
            check = true;
        }
        return check;
    }

}
