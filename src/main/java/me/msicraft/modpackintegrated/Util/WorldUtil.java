package me.msicraft.modpackintegrated.Util;

import org.bukkit.GameRule;
import org.bukkit.World;

public class WorldUtil {

    public static void setBasicGamerules(World world) {
        world.setGameRule(GameRule.DO_FIRE_TICK, false);
        world.setGameRule(GameRule.DO_INSOMNIA, false);
        world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
        world.setGameRule(GameRule.SEND_COMMAND_FEEDBACK, false);
        world.setGameRule(GameRule.COMMAND_BLOCK_OUTPUT, false);
    }

}
