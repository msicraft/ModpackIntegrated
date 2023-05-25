package me.msicraft.modpackintegrated;

import me.msicraft.modpackintegrated.Command.MainCommand;
import me.msicraft.modpackintegrated.Command.MainTabComplete;
import me.msicraft.modpackintegrated.CraftingEquip.Event.CraftingEquipEvent;
import me.msicraft.modpackintegrated.CraftingEquip.Event.CraftingEquipInvClick;
import me.msicraft.modpackintegrated.CraftingEquip.Util.CraftingEquipStatUtil;
import me.msicraft.modpackintegrated.DataFile.SpecialAbilityInfoFile;
import me.msicraft.modpackintegrated.EntityBlockBreak.Event.EntityBlockBreakSpawn;
import me.msicraft.modpackintegrated.EntityScaling.Event.EntityScalingRelated;
import me.msicraft.modpackintegrated.Event.EntityRelated;
import me.msicraft.modpackintegrated.Event.PlayerJoinAndQuit;
import me.msicraft.modpackintegrated.Event.PlayerRelated;
import me.msicraft.modpackintegrated.Event.WorldRelated;
import me.msicraft.modpackintegrated.KillPoint.Event.PlayerKillEntityEvent;
import me.msicraft.modpackintegrated.KillPoint.KillPointUtil;
import me.msicraft.modpackintegrated.MainMenu.Event.MainMenuEvent;
import me.msicraft.modpackintegrated.MainMenu.KillPointShop.Skill.KillPointShopSkill;
import me.msicraft.modpackintegrated.Util.WorldUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class ModPackIntegrated extends JavaPlugin {

    private static ModPackIntegrated plugin;
    public static SpecialAbilityInfoFile specialAbilityInfoFile;

    public static ModPackIntegrated getPlugin() { return plugin; }
    public static String getPrefix() { return "[ModPack Integrated]"; }
    public static boolean isDebugEnabled = false;

    public static final Map<UUID, ItemStack> exportEnchantMap = new HashMap<>();
    private final ItemStack airStack = new ItemStack(Material.AIR, 1);
    public static String bukkitVersion;

    @Override
    public void onEnable() {
        plugin = this;
        specialAbilityInfoFile = new SpecialAbilityInfoFile(this);
        createConfigFile();
        configFilesReload();
        eventsRegister();
        commandsRegister();
        String version = getServer().getBukkitVersion();
        String[] a = version.split("-");
        bukkitVersion = a[0];
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.isOnline()) {
                killPointUtil.loadKillPoint(player);
                killPointUtil.registerKillPointTask(player);
                craftingEquipStatUtil.registerStatMapTask(player);
                craftingEquipStatUtil.registerStatMap(player);
                if (!exportEnchantMap.containsKey(player.getUniqueId())) {
                    exportEnchantMap.put(player.getUniqueId(), airStack);
                }
                PlayerJoinAndQuit.registerTimerTask(player);
                if (isDebugEnabled) {
                    getServer().getConsoleSender().sendMessage("온라인 플레이어에 대한 작업 수행");
                    getServer().getConsoleSender().sendMessage("플레이어: " + player);
                }
            }
        }
        BukkitTask tabListTask = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : getServer().getOnlinePlayers()) {
                    if (player.isOnline()) {
                        World world = player.getWorld();
                        String worldName = world.getName();
                        if (worldName.equals("DIM-1")) {
                            worldName = "Nether";
                        } else if (worldName.equals("DIM1")) {
                            worldName = "End";
                        }
                        player.setPlayerListName(ChatColor.WHITE + player.getName() +
                                " (" + ChatColor.AQUA + worldName + " | " + WorldUtil.getWorldTimeTo24Format(world.getTime()) +
                                ChatColor.WHITE + ")");
                    }
                }
            }
        }.runTaskTimer(this, 20L, 100L);
        if (isDebugEnabled) {
            Bukkit.getConsoleSender().sendMessage("탭 리스트 월드 위치 표시 작업 스케쥴러 등록: " + tabListTask.getTaskId());
            Bukkit.getConsoleSender().sendMessage("버킷 버전: " + bukkitVersion);
        }
        Bukkit.getConsoleSender().sendMessage("Max Ram: " + Runtime.getRuntime().maxMemory() / 1024L / 1024L);
        getServer().getConsoleSender().sendMessage(getPrefix() + ChatColor.GREEN + " Plugin Enabled");
    }

    @Override
    public void onDisable() {
        killPointUtil.saveAllKillPoint();
        for (UUID uuid : exportEnchantMap.keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                player.getInventory().addItem(exportEnchantMap.get(player.getUniqueId()));
            }
        }
        getServer().getConsoleSender().sendMessage(getPrefix() + ChatColor.RED + " Plugin Disabled");
    }

    private void eventsRegister() {
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new PlayerJoinAndQuit(), this);
        pluginManager.registerEvents(new PlayerKillEntityEvent(), this);
        pluginManager.registerEvents(new PlayerRelated(), this);
        pluginManager.registerEvents(new WorldRelated(), this);
        pluginManager.registerEvents(new MainMenuEvent(), this);
        pluginManager.registerEvents(new EntityRelated(), this);
        pluginManager.registerEvents(new CraftingEquipInvClick(), this);
        pluginManager.registerEvents(new CraftingEquipEvent(), this);
        pluginManager.registerEvents(new EntityScalingRelated(), this);
        pluginManager.registerEvents(new EntityBlockBreakSpawn(), this);
    }

    private void commandsRegister() {
        PluginCommand pluginCommand = getServer().getPluginCommand("modpackintegrated");
        if (pluginCommand != null) {
            pluginCommand.setExecutor(new MainCommand());
            pluginCommand.setTabCompleter(new MainTabComplete());
        }
    }

    private final KillPointUtil killPointUtil = new KillPointUtil();
    private final CraftingEquipStatUtil craftingEquipStatUtil = new CraftingEquipStatUtil();

    public void configFilesReload() {
        reloadConfig();
        killPointUtil.reloadVariables();
        PlayerKillEntityEvent.reloadVariables();
        WorldRelated.reloadVariables();
        PlayerRelated.reloadVariables();
        EntityRelated.reloadVariables();
        KillPointShopSkill.reloadVariables();
        CraftingEquipEvent.reloadVariables();
        specialAbilityInfoFile.reloadConfig();
        EntityScalingRelated.reloadVariables();
        isDebugEnabled = getPlugin().getConfig().contains("Debug-Enabled") && getPlugin().getConfig().getBoolean("Debug-Enabled");
    }

    protected FileConfiguration config;

    private void createConfigFile() {
        File configf = new File(getDataFolder(), "config.yml");
        if (!configf.exists()) {
            configf.getParentFile().mkdirs();
            saveResource("config.yml", false);
        }
        FileConfiguration config = new YamlConfiguration();
        try {
            config.load(configf);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    private void replaceConfig() {
        File file = new File(getDataFolder(), "config.yml");
        this.config = YamlConfiguration.loadConfiguration(file);
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        File config_old = new File(getDataFolder(),"config_old-" + dateFormat.format(date) + ".yml");
        file.renameTo(config_old);
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + getPrefix() + " Plugin replaced the old config.yml with config_old.yml and created a new config.yml");
    }

}
