/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.api.structure.event.build;

import com.sc.api.structure.event.structure.StructureEvent;
import com.sc.api.structure.model.structure.Structure;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

/**
 * Fired when an entity used its resources (an itemstack) to build a structure.
 * @author Chingo
 */
public abstract class BuildEvent extends StructureEvent {

    private final Entity entity;
    private final ItemStack stack;

    /**
     * Constructor.
     *
     * @param structure The structure
     * @param entity The entity involved in this event
     * @param stack The itemstack that was commited
     */
    public BuildEvent(final Structure structure, final Entity entity, final ItemStack stack) {
        super(structure);
        this.entity = entity;
        this.stack = new ItemStack(stack);
    }

    /**
     * Gets the entity involved in this event.
     *
     * @return The entity involved in this event
     */
    public Entity getEntity() {
        return entity;
    }

    /**
     * Gets a copy of the itemstack that was commited.
     *
     * @return The itemstack that was commited to the structure
     */
    public ItemStack getStack() {
        return new ItemStack(stack);
    }

}
