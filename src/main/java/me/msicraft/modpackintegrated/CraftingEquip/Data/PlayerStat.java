package me.msicraft.modpackintegrated.CraftingEquip.Data;

import me.msicraft.modpackintegrated.CraftingEquip.Util.CraftingEquipStatUtil;
import org.bukkit.entity.Player;

import java.util.Map;

public class PlayerStat {

    private Player player;
    private double addMelee,addProjectile,addAttackSpeed,addDefense,addHealth;

    public PlayerStat(Player player) {
        this.player = player;
        if (CraftingEquipStatUtil.containStatMap(player)) {
            Map<String, Double> statMap = CraftingEquipStatUtil.getStatMap(player);
            addMelee = statMap.get("MeleeDamage");
            addProjectile = statMap.get("ProjectileDamage");
            addAttackSpeed = statMap.get("AttackSpeed");
            addDefense = statMap.get("Defense");
            addHealth = statMap.get("Health");
        } else {
            addMelee = 0;
            addProjectile = 0;
            addAttackSpeed = 0;
            addDefense = 0;
            addHealth = 0;
        }
    }

    public Player getPlayer() {
        return player;
    }

    public double getAddMelee() {
        return addMelee;
    }

    public double getAddProjectile() {
        return addProjectile;
    }

    public double getAddAttackSpeed() {
        return addAttackSpeed;
    }

    public double getAddDefense() {
        return addDefense;
    }

    public double getAddHealth() {
        return addHealth;
    }

}
