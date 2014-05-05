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

/**
 *
 * @author Chingo
 */
public class SCCore {

    private static SCCore instance;
    
    private SCCore(){}
    
    public static SCCore getInstance() {
        if(instance == null) {
            instance = new SCCore();
        }
        return instance;
    }

    public void initDB() {
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
