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

package com.chingo247.settlercraft.structureapi.plan.schematic;


import com.chingo247.settlercraft.structureapi.plan.StructurePlan;
import com.chingo247.settlercraft.structureapi.structure.plan.SettlerCraftPlan;
import com.chingo247.settlercraft.structureapi.world.Direction;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.data.DataException;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Chingo
 */
public class SchematicManager {
    
    private final Map<Long, Schematic> schematics;
    
    private static SchematicManager instance;
    
    SchematicManager() {
        this.schematics = Collections.synchronizedMap(new HashMap<Long, Schematic>());
    }
    
    public static SchematicManager getInstance() {
        if(instance == null) {
            instance = new SchematicManager();
        }
        return instance;
    }
    
    /**
     * Loads the schematic from the file. If there is a cached instance of the schematic available, the cached instance is returned
     * @param schematic The schematic file
     * @return The schematic for this file
     * @throws IOException
     * @throws DataException 
     */
    public Schematic getOrLoad(File schematic) throws IOException, DataException {
        long checksum = FileUtils.checksumCRC32(schematic);
        Schematic s = schematics.get(checksum);
        if(s != null) {
            return s;
        }
        s = Schematic.load(schematic);
        schematics.put(checksum, s);
        
        return s;
    }
    
    public Schematic getOrLoad(SettlerCraftPlan plan) throws IOException, DataException {
        return getOrLoad(plan.getSchematic());
    }
    
    public CuboidClipboard getClipboard(Long checksum) {
        throw new UnsupportedOperationException(); // UNMODIFIABLE CLIPBOARD!
    }
    
    public CuboidClipboard getClipboard(Long checksum, Direction direction) {
        throw new UnsupportedOperationException(); // UNMODIFIABLE CLIPBOARD!
    }
    
    public boolean hasSchematic(long checksum) {
        return schematics.get(checksum) != null;
    }
    
    public boolean hasSchematic(StructurePlan plan) throws IOException {
        long checkskum = FileUtils.checksumCRC32(plan.getSchematic());
        return hasSchematic(checkskum);
    }

}
