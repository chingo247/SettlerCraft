/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose SettlerCraftTools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.plugin;

import com.settlercraft.exception.InvalidStructurePlanException;
import com.settlercraft.listener.PlayerListener;
import com.settlercraft.listener.StructurePlanListener;
import com.settlercraft.model.entity.structure.Structure;
import com.settlercraft.model.entity.structure.StructureChest;
import com.settlercraft.model.entity.structure.StructureEntity;
import com.settlercraft.model.entity.structure.StructureProgress;
import com.settlercraft.model.entity.structure.StructureProgressSign;
import com.settlercraft.model.plan.requirement.material.LayerRequirement;
import com.settlercraft.model.plan.requirement.material.ResourceRequirement;
import com.settlercraft.model.plan.requirement.material.SpecialResourceRequirement;
import com.settlercraft.model.recipe.SettlerCraftBuildingPlans;
import com.settlercraft.model.recipe.SettlerCraftTools;
import com.settlercraft.util.HibernateUtil;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Chingo
 */
public class SettlerCraft extends JavaPlugin {

    public static final String name = "SettlerCraft";
    private static StructurePlanRegister structurePlanRegister;

    @Override
    public void onEnable() {
        if (getServer().getPluginManager().getPlugin("Citizens") == null || getServer().getPluginManager().getPlugin("Citizens").isEnabled() == false) {
            getLogger().log(Level.SEVERE, "Citizens 2.0 not found or not enabled");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        registerBuildings();
        registerRecipes();
        registerListeners();

        initDB();
    }

    @Override
    public void onLoad() {

    }

    private void registerBuildings() {
        File buildingFolder = new File(getDataFolder().getAbsolutePath());
        if (!buildingFolder.exists()) {
            buildingFolder.mkdir();
        }
        try {
            structurePlanRegister = new StructurePlanRegister(buildingFolder);
        } catch (InvalidStructurePlanException ex) {
            Logger.getLogger(SettlerCraft.class.getName()).log(Level.SEVERE, null, ex);
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    public static StructurePlanRegister getStructurePlanRegister() {
        return structurePlanRegister;
    }

    private void registerRecipes() {
        SettlerCraftBuildingPlans.load(this);
        SettlerCraftTools.load(this);
    }

    private void initDB() {
        HibernateUtil.addAnnotatedClasses(
                Structure.class,
                StructureChest.class,
                StructureEntity.class,
                StructureProgressSign.class,
                StructureProgress.class,
                ResourceRequirement.class,
                SpecialResourceRequirement.class,
                LayerRequirement.class
        );
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new StructurePlanListener(this), this);
//        Bukkit.getPluginManager().registerEvents(new StructureChestListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
    }

}
