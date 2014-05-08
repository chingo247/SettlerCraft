/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.api.structure.event.build;

import com.sc.api.structure.model.structure.Structure;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Chingo
 */
public class PlayerBuildEvent extends BuildEvent {

    private final Player player;

    /**
     * Constructor.
     *
     * @param structure The structure involved in this event
     * @param player The player involved in this event
     * @param stack The itemstack involved in this event
     */
    public PlayerBuildEvent(Structure structure, Player player, ItemStack stack) {
        super(structure, player, stack);
        this.player = player;
    }

    /**
     * Gets the player who is involved in this event.
     *
     * @return The player who is involved in this event
     */
    public Player getPlayer() {
        return player;
    }
    
    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
