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
package com.sc.api.structure.plan;

import com.google.common.io.Files;
import com.sc.api.structure.entity.plan.StructurePlan;
import com.sc.api.structure.entity.plan.StructureSchematic;
import com.sc.api.structure.persistence.service.SchematicService;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.CRC32;

/**
 *
 * @author Chingo
 */
public class PlanManager {

    private final Map<String, StructurePlan> plans;
    private final ConcurrentHashMap<Long, CuboidClipboard> schematics;
    private static PlanManager instance;
    private final SchematicService ss = new SchematicService();

    private PlanManager() {
        this.plans = new HashMap<>();
        this.schematics = new ConcurrentHashMap<>();
    }

    public static PlanManager getInstance() {
        if (instance == null) {
            instance = new PlanManager();
        }
        return instance;
    }

    public List<StructurePlan> getPlans() {
        return new ArrayList<>(plans.values());
    }

    public CuboidClipboard getClipBoard(Long checksum) {
        if (schematics.containsKey(checksum)) {
            return schematics.get(checksum);
        } else {
            StructureSchematic schematic = ss.getSchematic(checksum);
            if (schematic != null) {
                try {
                    schematics.put(checksum, SchematicFormat.MCEDIT.load(schematic.getSchematic()));
                    return schematics.get(checksum);
                } catch (IOException | DataException ex) {
                    Logger.getLogger(PlanManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            return null;
        }
    }

    public void add(StructurePlan plan, File schematic) throws StructurePlanException {
        if (plans.containsKey(plan.getId().trim())) {
            throw new StructurePlanException("Plan id: " + plan.getId() + " already in use!");
        } else {
            try {
                //            System.out.println("PlanId :" + plan.getId().trim() + ":");
                Long checksum = Files.getChecksum(schematic, new CRC32());
                plans.put(plan.getId().trim(), plan);
                if (!schematics.containsKey(checksum)) {
                    schematics.put(checksum, SchematicFormat.MCEDIT.load(schematic));
                }
                StructureSchematic sts = new StructureSchematic(schematic);
                if (!ss.exists(sts)) {
                    ss.save(sts);
                }
            } catch (IOException | DataException ex) {
                Logger.getLogger(PlanManager.class.getName()).log(Level.SEVERE, null, ex);
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
