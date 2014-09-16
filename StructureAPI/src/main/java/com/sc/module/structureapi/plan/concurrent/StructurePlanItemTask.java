/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.module.structureapi.plan.concurrent;

import com.sc.module.structureapi.plan.StructurePlan;
import com.sc.module.structureapi.plan.StructurePlanItem;
import com.sc.module.structureapi.plan.StructurePlanManager;
import com.sk89q.worldedit.data.DataException;
import construction.exception.StructurePlanException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dom4j.DocumentException;

/**
 *
 * @author Chingo
 */
public abstract class StructurePlanItemTask implements Runnable {
    
    public final StructurePlan plan;

        public StructurePlanItemTask(StructurePlan plan) {
            this.plan = plan;
        }

        @Override
        public void run() {
            try {
                StructurePlanItem item = StructurePlanItem.load(plan);
                onComplete(item);
            } catch (IOException | DataException | DocumentException | StructurePlanException ex) {
                Logger.getLogger(StructurePlanManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        public abstract void onComplete(StructurePlanItem item);
    
}
