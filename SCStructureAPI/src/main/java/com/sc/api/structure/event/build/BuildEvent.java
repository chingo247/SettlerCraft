/*
 * Copyright (C) 2014 Chingo
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.sc.api.structure.event.build;

import com.sc.api.structure.event.structure.StructureEvent;
import com.sc.api.structure.model.Structure;
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
