
/*
 * The MIT License
 *
 * Copyright 2015 Chingo.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.chingo247.settlercraft.structureapi.plan.schematic;


import com.chingo247.settlercraft.structureapi.plan.StructurePlan;
import com.chingo247.settlercraft.structureapi.world.Direction;
import com.chingo247.settlercraft.util.SchematicUtil;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.data.DataException;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Chingo
 */
public class SchematicManager {
    
    private static final Direction DEFAULT_DIRECTION = Direction.EAST; // By default schematics are pointing to east
    private final Map<Long, Schematic> schematics;
    private final Map<Long, EnumMap<Direction, UnModifiableClipboard>> clipboards;
    
    private static SchematicManager instance;
    
    SchematicManager() {
        this.schematics = Collections.synchronizedMap(new HashMap<Long, Schematic>());
        this.clipboards = new HashMap<>();
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
    
    public Schematic getOrLoad(StructurePlan plan) throws IOException, DataException {
        return getOrLoad(plan.getSchematicFile());
    }
    
    public CuboidClipboard getClipboard(Long checksum) {
        return getClipboard(checksum, DEFAULT_DIRECTION);
    }
    
    public CuboidClipboard getClipboard(Long checksum, Direction direction) {
        synchronized(clipboards.get(checksum)) {
            if(clipboards.get(checksum) == null) {
                return null;
            }
        }
        
        synchronized(clipboards.get(checksum).get(direction)) {
            CuboidClipboard clipboard = clipboards.get(checksum).get(direction);
            if(clipboard != null) {
                return clipboard;
            } else if (schematics.get(checksum) == null) {
                return null;
            } else {
                
                Schematic schematic = schematics.get(checksum);
                
                try {
                    CuboidClipboard cc = schematic.getClipboard();
                    SchematicUtil.align(cc, direction);
                    UnModifiableClipboard ucc = new UnModifiableClipboard(cc);
                    clipboards.get(checksum).put(direction, ucc);
                    return ucc;
                } catch (IOException | DataException ex) { // Should never happen on existing clipboards that have succesfully been read once
                    Logger.getLogger(SchematicManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return null;
    }
    
    public boolean hasSchematic(long checksum) {
        return schematics.get(checksum) != null;
    }
    
    public boolean hasSchematic(StructurePlan plan) throws IOException {
        long checkskum = FileUtils.checksumCRC32(plan.getSchematic());
        return hasSchematic(checkskum);
    }

}
