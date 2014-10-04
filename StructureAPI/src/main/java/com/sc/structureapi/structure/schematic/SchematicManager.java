/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.structureapi.structure.schematic;

import com.sc.structureapi.structure.plan.StructurePlan;
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
    
    private final Map<Long, Schematic> schematics = Collections.synchronizedMap(new HashMap<Long, Schematic>());
    private static SchematicManager instance;
    
    private SchematicManager() {}
    
    public Schematic getSchematic(File schematic) throws IOException, DataException {
        long checksum = FileUtils.checksumCRC32(schematic);
        Schematic s = schematics.get(checksum);
        if(s != null) {
            return s;
        }
        s = Schematic.load(schematic);
        schematics.put(checksum, s);
        
        return s;
    }
    
    /**
     * Loads the schematic and stores it in the hashmap, from which it's available by the file's checksum
     * @param schematic
     * @throws IOException
     * @throws DataException 
     */
    public void load(File schematic) throws IOException, DataException {
        long checksum = FileUtils.checksumCRC32(schematic);
        if(schematics.get(checksum) != null) {
            Schematic s = Schematic.load(schematic);
            schematics.put(s.getCheckSum(), s);
        }
    }
    
    public Schematic getSchematic(long checksum) {
        return schematics.get(checksum);
    }
    
    public boolean hasSchematic(long checksum) {
        return schematics.get(checksum) != null;
    }
    
    public boolean hasSchematic(StructurePlan plan) throws IOException {
        long checkskum = FileUtils.checksumCRC32(plan.getSchematic());
        return hasSchematic(checkskum);
    }
    
    public static SchematicManager getInstance() {
        if(instance == null) {
            instance = new SchematicManager();
        }
        return instance;
    }
    
    
    


    
}
