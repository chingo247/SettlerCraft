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
package com.chingo247.settlercraft.structureapi.structure.complex;

import com.chingo247.settlercraft.structureapi.plan.schematic.SchematicData;
import com.chingo247.settlercraft.structureapi.plan.schematic.SchematicManager;
import com.chingo247.settlercraft.structureapi.structure.plan.SettlerCraftPlan;
import com.chingo247.settlercraft.structureapi.world.Dimension;
import java.io.File;
import java.util.List;

/**
 *
 * @author Chingo
 */
class SubstructureType {
    
    private String name;
    private SettlerCraftPlan plan;
    private Dimension dimension;
    private SubstructureType parent;
    private int level;
    private List<SubstructureType> structures;

    SubstructureType load(SettlerCraftPlan plan) {
        File schematic = plan.getSchematic();
        SchematicManager sm = SchematicManager.getInstance();
        sm.getOrLoad(schematic)
    }
    
    
    
    
    
    
    
       
    
}
