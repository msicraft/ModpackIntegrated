package me.msicraft.modpackintegrated.CustomEvent;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerAttackEvent extends Event {

    private final static HandlerList handlers = new HandlerList();

    private Player player;
    private Entity target;
    private double originalDamage;

    public PlayerAttackEvent(Player player, Entity target, double originalDamage) {
        this.player = player;
        this.target = target;
        this.originalDamage = originalDamage;
    }

    public Player getPlayer() {
        return player;
    }

    public Entity getTarget() {
        return target;
    }

    public double getOriginalDamage() {
        return originalDamage;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
