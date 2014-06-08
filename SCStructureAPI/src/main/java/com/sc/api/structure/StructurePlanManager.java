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
package com.sc.api.structure;

import com.google.common.io.Files;
import com.sc.api.structure.entity.plan.StructurePlan;
import com.sc.api.structure.entity.plan.StructureSchematic;
import com.sc.api.structure.persistence.service.SchematicService;
import com.sc.api.structure.plan.StructurePlanException;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.CRC32;

/**
 *
 * @author Chingo
 */
public class StructurePlanManager {

    private final ConcurrentHashMap<String, StructurePlan> plans;
    private final ConcurrentHashMap<Long, StructureSchematic> schematics;
    private static StructurePlanManager instance;
    private final SchematicService ss = new SchematicService();

    private StructurePlanManager() {
        this.plans = new ConcurrentHashMap<>();
        this.schematics = new ConcurrentHashMap<>();
    }

    public static StructurePlanManager getInstance() {
        if (instance == null) {
            instance = new StructurePlanManager();
        }
        return instance;
    }

    public List<StructurePlan> getPlans() {
        return new ArrayList<>(plans.values());
    }

    public CuboidClipboard getClipBoard(Long checksum) {
        StructureSchematic schematic = getSchematic(checksum);
        if(schematic != null) {
            try {
                return SchematicFormat.MCEDIT.load(schematic.getSchematic());
            } catch (IOException | DataException ex) {
                Logger.getLogger(StructurePlanManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        } 
        return null;
    }
    
    public StructureSchematic getSchematic(Long checksum) {
        if (schematics.containsKey(checksum)) {
            return schematics.get(checksum);
        } else {
            StructureSchematic schematic = ss.getSchematic(checksum);
            if (schematic != null) {
                schematics.put(checksum, schematic);
                return schematic;
            }
        }
        return null;
    }

    public void add(StructurePlan plan, File schematic) throws StructurePlanException {
        if (plans.containsKey(plan.getId().trim())) {
            throw new StructurePlanException("Plan id: " + plan.getId() + " already in use!");
        } else {
            try {
                Long checksum = Files.getChecksum(schematic, new CRC32());
                plans.put(plan.getId().trim(), plan);
                StructureSchematic sts = new StructureSchematic(schematic);
                if (!schematics.containsKey(checksum)) {
                    schematics.put(checksum, sts);
                }
               
                if (!ss.exists(sts)) {
                    ss.save(sts);
                }
            } catch (IOException ex) {
                Logger.getLogger(StructurePlanManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }

    public StructurePlan getPlan(String planId) {
        return plans.get(planId);
    }

    public boolean contains(StructurePlan plan) {
        return plans.containsKey(plan.getId());
    }

}
