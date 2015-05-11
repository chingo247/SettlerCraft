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
package com.chingo247.settlercraft.structureapi.structure.session;

import com.chingo247.settlercraft.structureapi.structure.Structure;
import com.chingo247.settlercraft.structureapi.structure.options.DemolishingOptions;
import com.chingo247.settlercraft.structureapi.structure.options.PlaceOptions;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.world.World;

/**
 *
 * @author Chingo
 */
public interface PlayerSession {
    
    public Player getPlayer();
    
    public Structure getSelectedStructure();
    
    public void selectStructure(World w, long id);
    
    public void selectStructure(Structure structure);
    
    public PlaceOptions getPlaceOptions();
    
    public DemolishingOptions getDemolishOptions();
    
    public void reset();
    
    public void deselectStructure();
    
}
