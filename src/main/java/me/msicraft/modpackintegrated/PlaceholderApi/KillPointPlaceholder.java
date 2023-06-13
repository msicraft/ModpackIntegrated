package me.msicraft.modpackintegrated.PlaceholderApi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.msicraft.modpackintegrated.KillPoint.KillPointUtil;
import me.msicraft.modpackintegrated.KillPoint.Task.KillPointTask;
import me.msicraft.modpackintegrated.ModPackIntegrated;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class KillPointPlaceholder extends PlaceholderExpansion {

    ModPackIntegrated plugin;

    public KillPointPlaceholder(ModPackIntegrated plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "mpi";
    }

    @Override
    public @NotNull String getAuthor() {
        return "msicraft";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    private double getKillPointNextLevelToExpPercent(Player player) {
        double cal = KillPointUtil.getKillPointExp(player) / KillPointTask.requiredKillPointExp * 100.0;
        return Math.round(cal * 100.0) / 100.0;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (player.isOnline()) {
            Player onlineP = player.getPlayer();
            if (onlineP != null) {
                if (params.equalsIgnoreCase("killpoint")) {
                    return String.valueOf(KillPointUtil.getKillPoint(onlineP));
                }
                if (params.equalsIgnoreCase("killpointexp")) {
                    return ((int) getKillPointNextLevelToExpPercent(onlineP)) + "%";
                }
            }
        }
        return null;
    }

}
