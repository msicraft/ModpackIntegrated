package me.msicraft.modpackintegrated.EntityScaling;

import me.msicraft.modpackintegrated.ModPackIntegrated;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class EntityScalingUtil {

    private static final String scalingTagKey = "MPI-EntityScaling";
    private static final String scalingDamageTagKey = "MPI-EntityScaling-AddDamage";

    public static double getRandomValueDouble(double max, double min) {
        double randomValue = (Math.random() * (max - min)) + min;
        return (Math.floor(randomValue * 100) / 100.0);
    }

    public static boolean isScalingEntity(LivingEntity livingEntity) {
        boolean check = false;
        PersistentDataContainer data = livingEntity.getPersistentDataContainer();
        if (data.has(new NamespacedKey(ModPackIntegrated.getPlugin(), scalingTagKey), PersistentDataType.STRING)) {
            check = true;
        }
        return check;
    }

    public static void applyEntityScalingTag(LivingEntity livingEntity) {
        PersistentDataContainer data = livingEntity.getPersistentDataContainer();
        data.set(new NamespacedKey(ModPackIntegrated.getPlugin(), scalingTagKey), PersistentDataType.STRING, "ScalingEntity");
    }

    public static void applyRandomPercentDamageTag(LivingEntity livingEntity, double value) {
        PersistentDataContainer data = livingEntity.getPersistentDataContainer();
        data.set(new NamespacedKey(ModPackIntegrated.getPlugin(), scalingDamageTagKey), PersistentDataType.STRING, String.valueOf(value));
    }

    public static double getPercentDamage(LivingEntity livingEntity) {
        double v = 0;
        PersistentDataContainer data = livingEntity.getPersistentDataContainer();
        if (data.has(new NamespacedKey(ModPackIntegrated.getPlugin(), scalingDamageTagKey), PersistentDataType.STRING)) {
            String g = data.get(new NamespacedKey(ModPackIntegrated.getPlugin(), scalingDamageTagKey), PersistentDataType.STRING);
            if (g != null) {
                v = Double.parseDouble(g);
            }
        }
        return v;
    }

}
