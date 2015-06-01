/*
 * Copyright (C) 2015 Chingo
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
package com.chingo247.settlercraft.structureapi.event.async;

import com.sk89q.worldedit.world.World;

/**
 * Fired when a StructureJob has been completed, a StructureJob is a Build/Demolish operation 
 * that has been added to the AsyncWorldEdit's BlockPlacer's queue
  * Note that this event is fired Async! Therefore only available for subscribers registered at {@link AsyncEventManager}!
 * @author Chingo
 */
public class StructureJobCompleteEvent extends StructureJobEvent {

    public StructureJobCompleteEvent(World w, long structure, int jobid) {
        super(w, structure, jobid);
    }
    
}
