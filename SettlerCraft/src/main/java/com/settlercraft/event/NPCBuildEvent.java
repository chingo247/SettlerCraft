/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.settlercraft.event;

import com.settlercraft.model.entity.structure.Structure;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Chingo
 */
public class NPCBuildEvent extends BuildEvent{
    private NPC npc;

    public NPCBuildEvent(Structure structure, NPC npc, ItemStack stack) {
        super(structure, npc.getEntity(), stack);
        this.npc = npc;
    }
    
    
}
