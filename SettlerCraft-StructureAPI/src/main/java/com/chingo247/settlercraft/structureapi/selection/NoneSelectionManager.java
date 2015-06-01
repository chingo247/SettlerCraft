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
import com.sk89q.worldedit.regions.CuboidRegion;

/**
 * A Non-selection manager. Which will print the current selection to the player
 * @author Chingo
 */
public class NoneSelectionManager extends ASelectionManager {
    
    private static NoneSelectionManager instance;
    
    private NoneSelectionManager() {}
    
    public static NoneSelectionManager getInstance() {
        if(instance == null) {
            instance = new NoneSelectionManager();
        }
        return instance;
    }

    @Override
    public void select(Player player, Vector start, Vector end) {
        CuboidRegion dimension = new CuboidRegion(start, end);
        player.print("You've selected area: " + dimension.getMinimumPoint()+ ", " + dimension.getMaximumPoint());
        putSelection(new Selection(player.getUniqueId(), start, end));
    }

    @Override
    public void deselect(Player player) {
        removeSelection(getSelection(player.getUniqueId()));
    }

    
    
}
