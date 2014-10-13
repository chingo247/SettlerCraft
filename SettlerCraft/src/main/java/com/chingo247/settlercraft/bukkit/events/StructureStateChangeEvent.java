/*
 * Copyright (C) 2014 Chingo247
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

package com.chingo247.settlercraft.bukkit.events;

import com.chingo247.settlercraft.structure.entities.structure.Structure;
import com.chingo247.settlercraft.structure.entities.structure.Structure.State;

/**
 * Called after the structure has changed state
 * @author Chingo
 */
public class StructureStateChangeEvent extends StructureEvent {
    
    private final State oldState;

    public StructureStateChangeEvent(Structure structure, State oldState) {
        super(structure);
        this.oldState = oldState;
    }

    public State getOldState() {
        return oldState;
    }
    
    
}
