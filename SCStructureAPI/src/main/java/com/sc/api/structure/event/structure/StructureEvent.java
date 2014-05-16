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

import com.sc.api.structure.model.structure.Structure;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 *
 * @author Chingo
 */
public class StructureEvent extends Event {
    
    private final Structure structure;

    public StructureEvent(Structure structure) {
        this.structure = structure;
    }

    public Structure getStructure() {
        return structure;
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
