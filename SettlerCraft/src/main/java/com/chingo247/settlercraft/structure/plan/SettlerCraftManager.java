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
package com.chingo247.settlercraft.structure.plan;

import com.chingo247.settlercraft.plugin.SettlerCraft;
import com.chingo247.settlercraft.structure.plan.document.PlanDataAPI;
import com.chingo247.settlercraft.structure.plan.document.PlanDocument;
import com.chingo247.settlercraft.structure.plan.document.StructureDocument;
import java.util.List;

/**
 *
 * @author Chingo
 */
public class SettlerCraftManager {
    
    private static SettlerCraftManager instance;

    private SettlerCraftManager() {
    }
    
    
    
    public static SettlerCraftManager getInstance() {
        if(instance == null) {
            instance = new SettlerCraftManager();
        }
        return instance;
    }
    
    
    
    public void initialize() {
        List<PlanDocument> planDocs = PlanDataAPI.getPlanDocuments(SettlerCraft.getInstance());
        // Load plans - Create StructurePlans
        long start = System.currentTimeMillis();
        SettlerCraftPlanManager.getInstance().load(planDocs);
        SettlerCraft.print("Loaded " + String.valueOf(SettlerCraftPlanManager.getInstance().getPlans().size()) + " plans in " + (System.currentTimeMillis() - start));
        
        // Load schematics - Add schematic-data to Database
        start = System.currentTimeMillis();
        SchematicManager.getInstance().loadData();
        SettlerCraft.print("Loaded " + String.valueOf(SettlerCraftPlanManager.getInstance().getPlans().size()) + " schematics in " + (System.currentTimeMillis() - start));
        
        // Load StructurePlanItems - Add them to the menu
        start = System.currentTimeMillis();
        PlanMenuManager.getInstance().loadPlans();
        SettlerCraft.print("Loaded " + String.valueOf(SettlerCraftPlanManager.getInstance().getPlans().size()) + " plan-items in " + (System.currentTimeMillis() - start));
        
//        List<StructureDocument> structureDocuments = PlanDataAPI.getStructureDocuments(SettlerCraft.getInstance());
//        for(StructureDocument d : structureDocuments) {
//            
//        }
    }
    
    public void loadStructure(StructureDocument structureDocument) {
        
    }
    
}
