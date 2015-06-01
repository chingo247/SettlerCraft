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
package com.chingo247.settlercraft.structureapi.selection;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.entity.Player;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 *
 * @author Chingo
 */
public abstract class ASelectionManager implements ISelectionManager {
    
    private Map<UUID, Selection> selections = Collections.synchronizedMap(new HashMap<UUID, Selection>());

    @Override
    public boolean hasSelection(Player player) {
        return selections.get(player.getUniqueId()) != null;
    }

    @Override
    public boolean matchesCurrentSelection(Player player, Vector start, Vector end) {
        Selection selection = selections.get(player.getUniqueId());
        if(selection == null) {
            return false;
        }
        Selection newSelection = new Selection(player.getUniqueId(), start, end);
        
        return selection.equals(newSelection);
    }
    
    protected Selection getSelection(UUID player) {
        if(player == null) return null;
        return selections.get(player);
    }
    
    protected void putSelection(Selection selection) {
        if(selection == null) return;
        selections.put(selection.getPlayer(), selection);
    }
    
    protected void removeSelection(Selection selection) {
        if(selection == null) return;
        selections.remove(selection.getPlayer());
    }
    
}
