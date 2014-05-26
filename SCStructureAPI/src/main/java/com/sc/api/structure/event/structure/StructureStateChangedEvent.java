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
package com.sc.api.structure.event.structure;

import com.sc.api.structure.entity.Structure;
import com.sc.api.structure.construction.progress.ConstructionState;
import org.bukkit.event.HandlerList;

/**
 * Automatically fired when the structure.setStatus() method is called
 *
 * @author Chingo
 */
public class StructureStateChangedEvent extends StructureEvent {

    private final ConstructionState oldState;

    public StructureStateChangedEvent(Structure structure, ConstructionState oldState) {
        super(structure);
        this.oldState = oldState;
    }

    public ConstructionState getOldState() {
        return oldState;
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
