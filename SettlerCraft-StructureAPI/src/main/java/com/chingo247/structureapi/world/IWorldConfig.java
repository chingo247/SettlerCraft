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
package com.chingo247.structureapi.world;

import java.util.UUID;

/**
 *
 * @author Chingo
 */
public interface IWorldConfig {
    
    void setVersion(String version);
    
    String getVersion();
    
    void setWorldUUID(String worldUUID);
    
    UUID getWorldUUID();
    
    
    /**
     * Checks if this world restricts structures to be build in ConstructionZones only
     * @see {@link ConstructionZone}
     * @return True if zones only
     */
    boolean isZonesOnly();
    
    /**
     * Will restrict structures to be build in zones only
     * @param zonesOnly Determines if zones only is enabled
     */
    void setZonesOnly(boolean zonesOnly);
    
    /**
     * This world doesn't allow structures whatsoever. Not even in ConstructionZones
     * @return True if this world doesnt allow any structures
     */
    boolean restrictsStructures();
    
    /**
     * Saves the config
     */
    void save();
    
    
    
}
