package me.msicraft.modpackintegrated.MainMenu;

import me.msicraft.modpackintegrated.KillPoint.KillPointUtil;
import me.msicraft.modpackintegrated.KillPoint.Task.KillPointTask;
import org.bukkit.entity.Player;

public class MainMenuUtil {

    public double getKillPointNextLevelToExpPercent(Player player) {
        double cal = KillPointUtil.getKillPointExp(player) / KillPointTask.requiredKillPointExp * 100.0;
        return Math.round(cal * 100.0) / 100.0;
    }

}
