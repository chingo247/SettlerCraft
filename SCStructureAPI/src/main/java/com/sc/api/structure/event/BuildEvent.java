/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.api.structure.event;


import com.settlercraft.core.event.SettlerCraftEvent;
import com.settlercraft.core.model.entity.structure.Structure;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

/**
 * Fired when an entity used its resources (an itemstack) to build a structure
 * @author Chingo
 */
public abstract class BuildEvent extends SettlerCraftEvent {
    
    private final Entity entity;
    private final ItemStack stack;
    private final Structure structure;

    public BuildEvent(Structure structure, Entity entity, ItemStack stack) {
        this.entity = entity;
        this.stack = new ItemStack(stack);
        this.structure = structure;
    }

    public Entity getEntity() {
        return entity;
    }

    public ItemStack getStack() {
        return stack;
    }

    public Structure getStructure() {
        return structure;
    }
    
    
    
    
    
    
}
