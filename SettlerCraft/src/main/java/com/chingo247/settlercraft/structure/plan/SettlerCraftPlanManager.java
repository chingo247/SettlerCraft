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
package com.chingo247.settlercraft.structure.plan;

import com.chingo247.settlercraft.exception.StructureDataException;
import com.chingo247.settlercraft.plugin.SettlerCraft;
import com.chingo247.settlercraft.structure.entities.structure.Structure;
import com.chingo247.settlercraft.structure.plan.document.PlanDataAPI;
import com.chingo247.settlercraft.structure.plan.document.PlanDocument;
import com.chingo247.settlercraft.structure.plan.document.PlanDocumentManager;
import com.chingo247.settlercraft.structure.plan.document.StructureDocument;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dom4j.DocumentException;

/**
 *
 * @author Chingo
 */
public class SettlerCraftPlanManager {

    public final File PLAN_FOLDER = new File(SettlerCraft.getInstance().getDataFolder(), "Plans");
    public final File SCHEMATIC_TO_PLAN_FOLDER = new File(SettlerCraft.getInstance().getDataFolder(), "SchematicToPlan");
    private static SettlerCraftPlanManager instance;
    private final Map<String, SettlerCraftPlan> plans = new HashMap<>();
    private final Map<Long, SettlerCraftPlan> structures = Collections.synchronizedMap(new HashMap<Long, SettlerCraftPlan>());
    private final ExecutorService executor = SettlerCraft.getInstance().getExecutorService();

    /**
     * Gets the instance of this API
     *
     * @return instance of SettlerCraftPlanManager
     */
    public static SettlerCraftPlanManager getInstance() {
        if (instance == null) {
            instance = new SettlerCraftPlanManager();
        }
        return instance;
    }

    /**
     * Creates the Folders for StructurePlans and SchematicToPlan if they don't exist
     */
    public void init() {
        if (!PLAN_FOLDER.exists()) {
            PLAN_FOLDER.mkdirs();
        }
        if (!SCHEMATIC_TO_PLAN_FOLDER.exists()) {
            SCHEMATIC_TO_PLAN_FOLDER.mkdirs();
        }
    }
    
    /**
     * Loads all plans multithreaded - blocks until all plans are loaded
     * @param planDocuments 
     */
    void load(List<PlanDocument> planDocuments) {
        this.plans.clear();
        
        
        List<Future> tasks = new LinkedList<>();
        for(final PlanDocument pd : planDocuments) {
            tasks.add(executor.submit(new Runnable() {

                @Override
                public void run() {
                    SettlerCraftPlan plan = new SettlerCraftPlan();
                    try {
                        plan.load(pd);
                        plans.put(plan.getRelativePath(), plan);
                    } catch (DocumentException | StructureDataException | IOException ex) {
                        Logger.getLogger(SettlerCraftPlanManager.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }));
        }
        
        // Blocks until all tasks are done
        for(Future task : tasks) {
            try {
                task.get();
            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(SettlerCraftPlanManager.class.getName()).log(Level.SEVERE, null, ex);
                for(Future f : tasks) {
                    f.cancel(true);
                }
            }
        }
        
    }
    
    synchronized void update(StructureDocument d) {
        SettlerCraftPlan p = new SettlerCraftPlan();
        try {
            p.load(d);
        } catch (DocumentException | StructureDataException | IOException ex) {
            Logger.getLogger(SettlerCraftPlanManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        structures.put(d.getStructure().getId(), p);
    }

    /**
     * Private Constructor
     */
    private SettlerCraftPlanManager() {
    }

//    public String getRelativePath(File config) {
//        String path = config.getAbsolutePath();
//        String minus = "\\plugins\\SettlerCraft\\";
//        path = path.substring(path.indexOf(minus) + minus.length());
//        int length = path.length();
//        path = path.substring(0, length - 4); // minus XML
//        return path;
//    }

    
    

    /**
     * Gets a plan by it's corresponding id
     *
     * @param id The id of the plan
     * @return The structure plan with the corresponding id
     */
    public SettlerCraftPlan getPlan(String id) {
        return plans.get(id);
    }

    public SettlerCraftPlan getPlan(Structure structure) throws StructureDataException, DocumentException {
        SettlerCraftPlan plan = structures.get(structure.getId());
        if (plan != null) {
            return plan;
        }
        File config = structure.getConfig();
        if (config == null) {
            throw new StructureDataException("Missing 'StructurePlan.xml' for structure: " + structure);
        }

        plan = null;
        StructureDocument d = PlanDataAPI.getStructureDocument(structure);
        if(d == null) {
            PlanDocumentManager.getInstance().register(structure);
            d = PlanDataAPI.getStructureDocument(structure);
        }
        
        
        try {
            plan = new SettlerCraftPlan();
            plan.load(d);
            structures.put(structure.getId(), plan);
        } catch (IOException ex) {
            Logger.getLogger(SettlerCraftPlanManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
        return plan;

    }

    /**
     * Gets the list of structureplans
     *
     * @return A list of structureplans
     */
    public List<SettlerCraftPlan> getPlans() {
        return new ArrayList<>(plans.values());
    }


}
