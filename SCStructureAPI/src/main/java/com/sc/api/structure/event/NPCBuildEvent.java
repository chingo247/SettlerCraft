/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.api.structure.event;

import com.settlercraft.core.model.entity.structure.Structure;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;


/**
 *
 * @author Chingo
 */
public class NPCBuildEvent extends BuildEvent{
    private NPC npc;

    /**
     * Constructor.
     * @param structure The structure involved in this event
     * @param npc The NPC involved in this event
     * @param stack The itemstack involved in this event
     */
    public NPCBuildEvent(final Structure structure, final NPC npc, final ItemStack stack) {
        super(structure, npc.getEntity(), stack);
        this.npc = npc;
    }

    /**
     * Gets the NPC involved in this event.
     * @return The NPC involved in this event
     */
    public NPC getNpc() {
        return npc;
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
