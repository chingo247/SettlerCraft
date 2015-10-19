/*
 * Copyright (C) 2015 ching
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
package com.chingo247.structureapi;

import com.chingo247.structureapi.model.zone.IConstructionZone;
import com.chingo247.xplatform.core.IPlayer;
import com.sk89q.worldedit.regions.CuboidRegion;

/**
 *
 * @author ching
 */
public interface IConstructionZoneManager {
    
    void checkWorldRestrictions(CuboidRegion region) throws RestrictionException;
    
    void checkConstructionZonePlacingRestrictions(CuboidRegion region) throws RestrictionException;
    
    IConstructionZone createZone(CuboidRegion region, IPlayer player) throws RestrictionException ;
    
}
