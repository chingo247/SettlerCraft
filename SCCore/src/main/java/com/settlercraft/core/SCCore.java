/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.core;

import com.settlercraft.core.model.entity.structure.Structure;
import com.settlercraft.core.model.entity.structure.StructureChest;
import com.settlercraft.core.model.entity.structure.StructureEntity;
import com.settlercraft.core.model.entity.structure.StructureProgressSign;
import com.settlercraft.core.model.entity.structure.construction.ConstructionSite;
import com.settlercraft.core.model.plan.requirement.material.LayerRequirement;
import com.settlercraft.core.model.plan.requirement.material.ResourceRequirement;
import com.settlercraft.core.model.plan.requirement.material.SpecialResourceRequirement;
import com.settlercraft.core.util.HibernateUtil;

/**
 *
 * @author Chingo
 */
public class SCCore {

    public static void initDB() {
        HibernateUtil.addAnnotatedClasses(
                Structure.class,
                StructureChest.class,
                StructureEntity.class,
                StructureProgressSign.class,
                ConstructionSite.class,
                ResourceRequirement.class,
                SpecialResourceRequirement.class,
                LayerRequirement.class
        );
    }

}
