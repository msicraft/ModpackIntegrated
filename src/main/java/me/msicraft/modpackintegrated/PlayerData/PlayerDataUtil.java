package me.msicraft.modpackintegrated.PlayerData;

import me.msicraft.modpackintegrated.PlayerData.File.PlayerDataFile;
import org.bukkit.entity.Player;

public class PlayerDataUtil {

    public void createAndRegisterFile(Player player) {
        PlayerDataFile playerDataFile = new PlayerDataFile(player);
        playerDataFile.createFile(player);
    }

}
