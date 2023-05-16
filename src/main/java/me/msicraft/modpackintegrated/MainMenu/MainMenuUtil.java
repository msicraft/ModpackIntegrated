package me.msicraft.modpackintegrated.MainMenu;

import me.msicraft.modpackintegrated.KillPoint.KillPointUtil;
import me.msicraft.modpackintegrated.KillPoint.Task.KillPointTask;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;

public class MainMenuUtil {

    public double getKillPointNextLevelToExpPercent(Player player) {
        double cal = KillPointUtil.getKillPointExp(player) / KillPointTask.requiredKillPointExp * 100.0;
        return Math.round(cal * 100.0) / 100.0;
    }

    public double getMaxHealth(Player player) {
        double v = 0;
        AttributeInstance instance = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (instance != null) {
            v = instance.getValue();
        }
        return v;
    }

    public double getAttackDamage(Player player) {
        double v = 0;
        AttributeInstance instance = player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
        if (instance != null) {
            v = instance.getValue();
        }
        return v;
    }

    public double getAttackSpeed(Player player) {
        double v = 0;
        AttributeInstance instance = player.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
        if (instance != null) {
            v = instance.getValue();
        }
        return v;
    }

    public double getArmor(Player player) {
        double v = 0;
        AttributeInstance instance = player.getAttribute(Attribute.GENERIC_ARMOR);
        if (instance != null) {
            v = instance.getValue();
        }
        return v;
    }

    public double getArmorToughness(Player player) {
        double v = 0;
        AttributeInstance instance = player.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS);
        if (instance != null) {
            v = instance.getValue();
        }
        return v;
    }

}
