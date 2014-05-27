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

import com.sc.api.structure.entity.progress.ConstructionTask.State;
import com.sc.api.structure.entity.Structure;
import org.bukkit.event.HandlerList;

/**
 * Automatically fired when the structure.setStatus() method is called
 *
 * @author Chingo
 */
public class StructureStateChangedEvent extends StructureEvent {

    private final State oldState;

    public StructureStateChangedEvent(Structure structure, State oldState) {
        super(structure);
        this.oldState = oldState;
    }

    public State getOldState() {
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
