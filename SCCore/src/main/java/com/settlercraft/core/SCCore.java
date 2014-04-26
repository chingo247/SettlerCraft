/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.core;

import com.settlercraft.core.model.entity.structure.Structure;
import com.settlercraft.core.model.entity.structure.StructureChest;
import com.settlercraft.core.model.entity.structure.StructureEntity;
import com.settlercraft.core.model.entity.structure.StructureProgress;
import com.settlercraft.core.model.entity.structure.StructureProgressSign;
import com.settlercraft.core.model.plan.requirement.material.MaterialResource;
import com.settlercraft.core.util.Database.HibernateUtil;
import com.settlercraft.core.util.Database.MemDBUtil;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Chingo
 */
public class SCCore extends SettlerCraftModule {

    public SCCore() {
        super("SCCore");
    }

    @Override
    protected void setupRecipes(JavaPlugin plugin) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void setupListeners(JavaPlugin plugin) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void init(JavaPlugin plugin) {
        HibernateUtil.addAnnotatedClasses(
                Structure.class,
                StructureChest.class,
                StructureEntity.class,
                StructureProgressSign.class,
                StructureProgress.class,
                MaterialResource.class
        );
        MemDBUtil.addAnnotatedClasses(
        );
    }


}
