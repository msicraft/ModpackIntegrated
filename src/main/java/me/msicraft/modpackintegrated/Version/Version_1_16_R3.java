package me.msicraft.modpackintegrated.Version;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;

public class Version_1_16_R3 {

    public static HoverEvent getHoverEventByShowItem(org.bukkit.inventory.ItemStack itemStack) {
        return new HoverEvent(HoverEvent.Action.SHOW_ITEM,new BaseComponent[]{new TextComponent(CraftItemStack.asNMSCopy(itemStack).save(new NBTTagCompound()).toString())});
    }

}
