/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.module.structureapi.plan;

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

    private Map<Long, Schematic> schematics = Collections.synchronizedMap(new HashMap<Long, Schematic>());
    private static SchematicManager instance;

    private SchematicManager() {
    }

    public static SchematicManager getInstance() {
        if (instance == null) {
            instance = new SchematicManager();
        }
        return instance;
    }

    void putSchematic(Schematic schematic) {
        schematics.put(schematic.getCheckSum(), schematic);
    }

    public Schematic getSchematic(StructurePlan plan) throws IOException, DataException {
        File schFile = plan.getSchematic();
        Schematic schematic = getSchematic(FileUtils.checksumCRC32(schFile));
        if (schematic == null) {
            schematic = Schematic.load(schFile);

            // Store/Cache it
            schematics.put(schematic.getCheckSum(), schematic);
        }
        return schematic;
    }

    public Schematic getSchematic(long checksum) {
        return schematics.get(checksum);
    }

}
