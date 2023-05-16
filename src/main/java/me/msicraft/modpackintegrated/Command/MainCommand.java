package me.msicraft.modpackintegrated.Command;

import me.msicraft.modpackintegrated.CraftingEquip.Enum.SpecialAbility;
import me.msicraft.modpackintegrated.CraftingEquip.Util.CraftingEquipStatUtil;
import me.msicraft.modpackintegrated.CraftingEquip.Util.CraftingEquipUtil;
import me.msicraft.modpackintegrated.KillPoint.KillPointUtil;
import me.msicraft.modpackintegrated.MainMenu.Inventory.MainMenuInv;
import me.msicraft.modpackintegrated.ModPackIntegrated;
import me.msicraft.modpackintegrated.Util.ExpUtil;
import me.msicraft.modpackintegrated.Util.WorldUtil;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

public class MainCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("modpackintegrated")) {
            if (args.length == 0) {
                sender.sendMessage(ChatColor.RED + "/modpackintegrated help");
            }
            if (args.length >= 1) {
                String var = args[0];
                if (var != null) {
                    switch (var) {
                        case "getexp" -> { //mpi getexp <player>
                            Player target = null;
                            try {
                                target = Bukkit.getPlayer(args[1]);
                            } catch (ArrayIndexOutOfBoundsException e) {
                                if (sender instanceof Player player) {
                                    target = player;
                                }
                            }
                            if (target != null) {
                                sender.sendMessage("Level: " + target.getLevel());
                                sender.sendMessage("TotalExp: " + ExpUtil.getPlayerExp(target));
                            }
                        }
                        case "getattribute" -> { //mpi getattribute <player> <attribute>
                            if (sender.isOp()) {
                                Player target = null;
                                try {
                                    target = Bukkit.getPlayer(args[1]);
                                } catch (ArrayIndexOutOfBoundsException e) {
                                    if (sender instanceof Player player) {
                                        target = player;
                                    }
                                }
                                if (target != null && target.isOnline()) {
                                    Attribute attribute = Attribute.GENERIC_MAX_HEALTH;
                                    try {
                                        attribute = Attribute.valueOf(args[2].toUpperCase());
                                    } catch (IllegalArgumentException | NullPointerException ignored) {
                                    }
                                    AttributeInstance instance = target.getAttribute(attribute);
                                    if (instance != null) {
                                        sender.sendMessage("Attribute: " + attribute.name());
                                        sender.sendMessage("플레이어: " + target);
                                        sender.sendMessage("값: " + instance.getValue());
                                        for (AttributeModifier modifier : instance.getModifiers()) {
                                            sender.sendMessage("Modifier: " + modifier.getName());
                                            sender.sendMessage("Amount: " + modifier.getAmount());
                                        }
                                    }
                                }
                            }
                        }
                        case "craftingequipment" -> { //mpi craftingequipment [ability,updateinventory] [get,set] [optional: abilityName]
                            if (sender instanceof Player player) {
                                if (player.isOp()) {
                                    try {
                                        String var2 = args[1];
                                        String var3 = args[2];
                                        SpecialAbility specialAbility;
                                        switch (var2) {
                                            case "ability" -> {
                                                switch (var3) {
                                                    case "get" -> {
                                                        ItemStack itemStack = player.getInventory().getItemInMainHand();
                                                        if (itemStack != null && itemStack.getType() != Material.AIR) {
                                                            player.sendMessage("특수 능력: " + CraftingEquipStatUtil.getSpecialAbility(itemStack));
                                                        }
                                                    }
                                                    case "set" -> {
                                                        try {
                                                            specialAbility = SpecialAbility.valueOf(args[3]);
                                                            ItemStack itemStack = player.getInventory().getItemInMainHand();
                                                            if (itemStack != null && itemStack.getType() != Material.AIR) {
                                                                if (CraftingEquipUtil.isCraftingEquipment(itemStack)) {
                                                                    CraftingEquipStatUtil.setSpecialAbility(specialAbility, itemStack);
                                                                    player.sendMessage(ChatColor.GREEN + "특수 능력이 설정되었습니다: " + ChatColor.GRAY + specialAbility.name());
                                                                } else {
                                                                    player.sendMessage(ChatColor.RED + "제작 장비가 아닙니다.");
                                                                }
                                                            }
                                                        } catch (Exception e) {
                                                            player.sendMessage(ChatColor.RED + "잘못된 특수능력");
                                                        }
                                                    }
                                                }
                                            }
                                            case "updateinventory" -> {
                                                Player target = null;
                                                try {
                                                    target = Bukkit.getPlayer(var3);
                                                } catch (ArrayIndexOutOfBoundsException e) {
                                                    target = player;
                                                }
                                                if (target != null) {
                                                    ItemStack[] itemStacks = target.getInventory().getContents();
                                                    for (ItemStack itemStack : itemStacks) {
                                                        if (itemStack != null && itemStack.getType() != Material.AIR) {
                                                            if (CraftingEquipUtil.isCraftingEquipment(itemStack)) {
                                                                CraftingEquipStatUtil.updateAbilityLore(itemStack);
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    } catch (ArrayIndexOutOfBoundsException e) {
                                        player.sendMessage(ChatColor.RED + "/mpi craftingequipment [get,set] [ability]");
                                    }
                                }
                            }
                        }
                        case "getbukkittask" -> {
                            if (args.length == 1 && sender.isOp()) {
                                if (sender.hasPermission("modpackintegrated.command.getbukkittask")) {
                                    List<BukkitTask> activeTasks = Bukkit.getScheduler().getPendingTasks();
                                    if (activeTasks.isEmpty()) {
                                        sender.sendMessage(ChatColor.RED + "등록된 스케쥴러 없음");
                                    } else {
                                        sender.sendMessage(ChatColor.YELLOW + "========================================");
                                        sender.sendMessage(ChatColor.GREEN + "스케쥴러 목록");
                                        for (BukkitTask bukkitTask : activeTasks) {
                                            sender.sendMessage("Plugin: " + bukkitTask.getOwner() + " | TaskId: " + bukkitTask.getTaskId());
                                        }
                                        sender.sendMessage(ChatColor.YELLOW + "========================================");
                                    }
                                }
                            }
                        }
                        case "help" -> {
                            if (args.length == 1) {
                                sender.sendMessage(ChatColor.YELLOW + "/modpackintegrated help : " + ChatColor.WHITE + "Show the list of commands");
                            }
                        }
                        case "reload" -> {
                            if (args.length == 1 && sender.hasPermission("modpackintegrated.command.reload")) {
                                ModPackIntegrated.getPlugin().configFilesReload();
                                sender.sendMessage(ModPackIntegrated.getPrefix() + ChatColor.GREEN + " Plugin config files reloaded");
                            }
                        }
                        case "setbasicgamerules" -> {
                            if (args.length == 1 && sender.isOp()) {
                                List<World> list = Bukkit.getWorlds();
                                for (World world : list) {
                                    sender.sendMessage(ChatColor.GREEN + "월드: " + ChatColor.GRAY + world.getName());
                                    WorldUtil.setBasicGamerules(world);
                                }
                                sender.sendMessage(ChatColor.GREEN + "총 " + ChatColor.GRAY + list.size() + ChatColor.GREEN + " 의 월드의 기본 게임룰 설절 적용 됨");
                            }
                        }
                        case "menu" -> { //mpi menu
                            if (sender instanceof Player player) {
                                MainMenuInv mainMenuInv = new MainMenuInv(player);
                                player.openInventory(mainMenuInv.getInventory());
                                mainMenuInv.setMainMenuInv(player);
                            }
                        }
                        case "setspawn" -> { //mpi setspawn
                            if (sender.hasPermission("modpackintegrated.command.setspawn")) {
                                if (sender instanceof Player player) {
                                    try {
                                        String var2 = args[1];
                                        if (var2.equals("null")) {
                                            ModPackIntegrated.getPlugin().getConfig().set("SpawnLocation", "");
                                            ModPackIntegrated.getPlugin().saveConfig();
                                            player.sendMessage(ChatColor.GREEN + "스폰 위치가 초기화 되었습니다.");
                                        }
                                    } catch (ArrayIndexOutOfBoundsException e) {
                                        Location location = player.getLocation();
                                        String s = player.getWorld().getName() + ":" + location.getX() + ":" + location.getY() + ":" + location.getZ() + ":" + location.getYaw() + ":" + location.getPitch();
                                        ModPackIntegrated.getPlugin().getConfig().set("SpawnLocation", s);
                                        ModPackIntegrated.getPlugin().saveConfig();
                                        player.sendMessage(ChatColor.GREEN + "스폰 위치가 변경되었습니다.");
                                        player.sendMessage(ChatColor.GREEN + "월드: " + player.getWorld().getName());
                                        player.sendMessage(ChatColor.GREEN + "X: " + location.getX() + " | Y: " + location.getY() + " | Z: " + location.getZ());
                                        player.getWorld().setSpawnLocation(location);
                                    }
                                }
                            }
                        }
                        case "killpoint" -> { //mpi killpoint [get,set,add] <player> [optional<value>]
                            if (sender.hasPermission("modpackintegrated.command.killpoint")) {
                                try {
                                    String var2 = args[1];
                                    Player player = Bukkit.getPlayer(args[2]);
                                    if (player != null && player.isOnline()) {
                                        int value;
                                        switch (var2) {
                                            case "get" -> {
                                                sender.sendMessage(ChatColor.GREEN + "Player: " + ChatColor.GRAY + player.getName());
                                                sender.sendMessage(ChatColor.GREEN + "KillPoint: " + ChatColor.GRAY + KillPointUtil.getKillPoint(player));
                                            }
                                            case "set" -> {
                                                value = Integer.parseInt(args[3]);
                                                sender.sendMessage(ChatColor.GREEN + "Player: " + ChatColor.GRAY + player.getName());
                                                KillPointUtil.setKillPoint(player, value);
                                                sender.sendMessage(ChatColor.GREEN + "Set KillPoint: " + ChatColor.GRAY + KillPointUtil.getKillPoint(player));
                                            }
                                            case "add" -> {
                                                value = Integer.parseInt(args[3]);
                                                sender.sendMessage(ChatColor.GREEN + "Player: " + ChatColor.GRAY + player.getName());
                                                sender.sendMessage(ChatColor.GREEN + "Before KillPoint: " + ChatColor.GRAY + KillPointUtil.getKillPoint(player));
                                                KillPointUtil.addKillPoint(player, value);
                                                sender.sendMessage(ChatColor.GREEN + "After KillPoint: " + ChatColor.GRAY + KillPointUtil.getKillPoint(player));
                                            }
                                        }
                                    }
                                } catch (ArrayIndexOutOfBoundsException e) {
                                    sender.sendMessage(ChatColor.RED + "/mpi killpoint [get,set,add] <player> [optional<value>]");
                                }
                            }
                        }
                        case "killpointexp" -> { //mpi killpointexp [get,set,add] <player>
                            if (sender.hasPermission("modpackintegrated.command.killpointexp")) {
                                try {
                                    String var2 = args[1];
                                    Player player = Bukkit.getPlayer(args[2]);
                                    if (player != null && player.isOnline()) {
                                        double value;
                                        switch (var2) {
                                            case "get" -> {
                                                sender.sendMessage(ChatColor.GREEN + "Player: " + ChatColor.GRAY + player.getName());
                                                sender.sendMessage(ChatColor.GREEN + "KillPointExp: " + ChatColor.GRAY + KillPointUtil.getKillPointExp(player));
                                            }
                                            case "set" -> {
                                                value = Double.parseDouble(args[3]);
                                                sender.sendMessage(ChatColor.GREEN + "Player: " + ChatColor.GRAY + player.getName());
                                                KillPointUtil.setKillPointExp(player, value);
                                                sender.sendMessage(ChatColor.GREEN + "Set KillPointExp: " + ChatColor.GRAY + KillPointUtil.getKillPointExp(player));
                                            }
                                            case "add" -> {
                                                value = Double.parseDouble(args[3]);
                                                sender.sendMessage(ChatColor.GREEN + "Player: " + ChatColor.GRAY + player.getName());
                                                sender.sendMessage(ChatColor.GREEN + "Before KillPointExp: " + ChatColor.GRAY + KillPointUtil.getKillPointExp(player));
                                                KillPointUtil.addKillPointExp(player, value);
                                                sender.sendMessage(ChatColor.GREEN + "After KillPointExp: " + ChatColor.GRAY + KillPointUtil.getKillPointExp(player));
                                            }
                                        }
                                    }
                                } catch (ArrayIndexOutOfBoundsException e) {
                                    sender.sendMessage(ChatColor.RED + "/mpi killpointexp [get,set,add] <player> [optional<value>]");
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

}
