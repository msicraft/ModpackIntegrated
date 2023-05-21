package me.msicraft.modpackintegrated.Command;

import me.msicraft.modpackintegrated.CraftingEquip.Enum.SpecialAbility;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class MainTabComplete implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("modpackintegrated")) {
            if (args.length == 1) {
                List<String> arguments = new ArrayList<>();
                arguments.add("menu");
                if (sender.isOp()) {
                    arguments.add("help");
                    arguments.add("reload");
                    arguments.add("killpoint");
                    arguments.add("killpointexp");
                    arguments.add("getbukkittask");
                    arguments.add("setbasicgamerules");
                    arguments.add("setspawn");
                    arguments.add("craftingequipment");
                    arguments.add("getattribute");
                    arguments.add("getexp");
                    arguments.add("fixentityhealth");
                }
                return arguments;
            }
            if (args.length == 2) {
                if (args[0].equals("killpoint") || args[0].equals("killpointexp")) {
                    List<String> arguments = new ArrayList<>();
                    if (sender.isOp()) {
                        arguments.add("get");
                        arguments.add("set");
                        arguments.add("add");
                    }
                    return arguments;
                }
            }
            if (args.length == 2) {
                if (args[0].equals("craftingequipment") && sender.isOp()) {
                    List<String> arguments = new ArrayList<>();
                    arguments.add("ability");
                    arguments.add("updateinventory");
                    arguments.add("totalkillpoint");
                    return arguments;
                }
            }
            if (args.length == 3) {
                if (args[0].equals("craftingequipment") && sender.isOp()) {
                    if (args[1].equals("ability")) {
                        List<String> arguments = new ArrayList<>();
                        arguments.add("get");
                        arguments.add("set");
                        return arguments;
                    }
                }
            }
            if (args.length == 4) {
                if (args[0].equals("craftingequipment") && sender.isOp()) {
                    if (args[1].equals("ability")) {
                        List<String> arguments = new ArrayList<>();
                        for (SpecialAbility specialAbility : SpecialAbility.values()) {
                            arguments.add(specialAbility.name());
                        }
                        return arguments;
                    }
                }
            }
            if (args.length == 3) {
                if (args[0].equals("getattribute") && sender.isOp()) {
                    List<String> attributes = new ArrayList<>();
                    for (Attribute attribute : Attribute.values()) {
                        attributes.add(attribute.name());
                    }
                    return attributes;
                }
            }
        }
        return null;
    }

}
