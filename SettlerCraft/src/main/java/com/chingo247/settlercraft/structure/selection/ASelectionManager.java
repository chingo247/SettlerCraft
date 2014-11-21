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
package com.chingo247.settlercraft.structure.selection;

import com.sk89q.worldedit.Vector;
import java.util.UUID;

/**
 *
 * @author Chingo
 */
public abstract class ASelectionManager {
    
    public abstract void select(UUID player, Vector pos1, Vector pos2);
    
    public abstract boolean hasSelection(UUID player);
    
    public abstract boolean matchesSelection(UUID player, Vector pos1, Vector pos2);
    
    public abstract void clearSelection(UUID player, boolean talk);
    
    public void clearSelection(UUID player) {
        clearSelection(player, false);
    }
    
}
