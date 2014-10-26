/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.structure;

import com.chingo247.settlercraft.plugin.ConfigProvider;
import com.chingo247.settlercraft.plugin.SettlerCraft;
import com.chingo247.structureapi.StructureAPI;
import com.chingo247.structureapi.plan.document.PlanDocument;
import com.sk89q.worldguard.protection.flags.Flag;
import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 * Implementation of The {@link StructureAPI} class
 * @author Chingo
 */
public class SettlerCraftStructureAPI extends StructureAPI {
    
    private static SettlerCraftStructureAPI instance;
    
    private final ConfigProvider configProvider;
    
    private final File STRUCTURE_PLAN_FOLDER;
    private final File STRUCTURE_DATA_FOLDER;
    private final File SCHEMATIC_TO_PLAN_FOLDER;
    
    public static SettlerCraftStructureAPI getInstance(SettlerCraft settlerCraft) {
        if(instance == null) {
            instance = new SettlerCraftStructureAPI(settlerCraft);
        }
        return instance;
    }

    private SettlerCraftStructureAPI(SettlerCraft settlerCraft) {
        super(settlerCraft, settlerCraft.getExecutorService());
        this.configProvider = settlerCraft.getConfigProvider();
        this.STRUCTURE_DATA_FOLDER = new File(settlerCraft.getDataFolder(), "Structures");
        this.STRUCTURE_PLAN_FOLDER = new File(settlerCraft.getDataFolder(), "Plans");
        this.SCHEMATIC_TO_PLAN_FOLDER = new File(settlerCraft.getDataFolder(), "SchematicToPlan");
        
        SCHEMATIC_TO_PLAN_FOLDER.mkdirs();
        STRUCTURE_DATA_FOLDER.mkdirs();
        STRUCTURE_PLAN_FOLDER.mkdirs();
    }

    @Override
    public HashMap<Flag, Object> getDefaultFlags() {
        return configProvider.getDefaultFlags();
    }

    @Override
    public int getBuildMode() {
        return configProvider.getBuildMode();
    }

    @Override
    public int getDemolisionMode() {
        return configProvider.getDemolisionMode();
    }

    @Override
    public boolean useHolograms() {
        return configProvider.useHolograms();
    }

    @Override
    public double getRefundPercentage() {
        return configProvider.getRefundPercentage();
    }

    @Override
    public File getStructureDataFolder() {
        return STRUCTURE_DATA_FOLDER;
    }

    @Override
    public File getPlanDataFolder() {
        return STRUCTURE_PLAN_FOLDER;
    }

    @Override
    public File getSchematicToPlanFolder() {
        return SCHEMATIC_TO_PLAN_FOLDER;
    }
    
    public void initialize() {
        // Generate plans
        getPlanDocumentGenerator().generate(SCHEMATIC_TO_PLAN_FOLDER);
        
        // Load plan documents
        long start = System.currentTimeMillis();
        getPlanDocumentManager().loadDocuments();
        StructureAPI.print("Loaded " + String.valueOf(getPlanDocumentManager().getDocuments().size()) + " PlanDocuments in " + (System.currentTimeMillis() - start));
        
        // Load structure documents
        start = System.currentTimeMillis();
        getStructureDocumentManager().loadDocuments();
        StructureAPI.print("Loaded " + String.valueOf(getStructureDocumentManager().getDocuments().size()) + " StructureDocuments in " + (System.currentTimeMillis() - start));
        
        // Load StructurePlans
        start = System.currentTimeMillis();
        List<PlanDocument> planDocs = getPlanDocumentManager().getDocuments();
        getStructurePlanManager().load(planDocs);
        StructureAPI.print("Loaded " + String.valueOf(getStructurePlanManager().getPlans().size()) + " StructurePlans in " + (System.currentTimeMillis() - start));
        
        // Load schematics - Add schematic-data to Database
        StructureAPI.print("Loading schematic data...");
        getSchematicManager().load();
    }
    
    
}
