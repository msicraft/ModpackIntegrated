package me.msicraft.modpackintegrated.CustomEvent;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerTakeDamageEvent extends Event {

    private final static HandlerList handlers = new HandlerList();

    private Player player;
    private Entity attacker;
    private double originalDamage;

    public PlayerTakeDamageEvent(Player player, Entity attacker, double originalDamage) {
        this.player = player;
        this.attacker = attacker;
        this.originalDamage = originalDamage;
    }

    public Player getPlayer() {
        return player;
    }

    public Entity getAttacker() {
        return attacker;
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
