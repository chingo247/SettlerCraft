/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.bukkit;

import com.chingo247.structureapi.StructureAPI;
import com.sk89q.worldguard.protection.flags.Flag;
import java.io.File;
import java.util.HashMap;

/**
 * Implementation of The {@link StructureAPI} class
 * @author Chingo
 */
public class BukkitSettlerCraftStructureAPI extends BukkitStructureAPI {
    
    private static BukkitSettlerCraftStructureAPI instance;
    
    private final ConfigProvider configProvider;
    
    private final File STRUCTURE_PLAN_FOLDER;
    private final File STRUCTURE_DATA_FOLDER;
    private final File SCHEMATIC_TO_PLAN_FOLDER;
    
    public static BukkitSettlerCraftStructureAPI getInstance(SettlerCraftPlugin settlerCraft) {
        if(instance == null) {
            instance = new BukkitSettlerCraftStructureAPI(settlerCraft);
        }
        return instance;
    }

    private BukkitSettlerCraftStructureAPI(SettlerCraftPlugin settlerCraft) {
        super(settlerCraft, settlerCraft.getExecutorService());
        this.configProvider = settlerCraft.getConfigProvider();
        this.STRUCTURE_DATA_FOLDER = new File(settlerCraft.getDataFolder(), "Structures");
        this.STRUCTURE_PLAN_FOLDER = new File(settlerCraft.getDataFolder(), "Plans");
        this.SCHEMATIC_TO_PLAN_FOLDER = new File(settlerCraft.getDataFolder(), "SchematicToPlan");
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
    
}
