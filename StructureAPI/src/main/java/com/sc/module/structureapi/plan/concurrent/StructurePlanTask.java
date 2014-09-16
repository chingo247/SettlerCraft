/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.module.structureapi.plan.concurrent;

import com.sc.module.structureapi.plan.StructurePlan;
import com.sc.module.structureapi.plan.StructurePlanManager;
import construction.exception.StructurePlanException;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Chingo
 */
 public abstract class StructurePlanTask implements Runnable {

        private final File xml;

        public StructurePlanTask(File xml) {
            this.xml = xml;
        }
        @Override
        public void run() {
            try {
                StructurePlan plan = StructurePlan.load(xml);
                
                
                onComplete(plan);
            } catch (StructurePlanException ex) {
                Logger.getLogger(StructurePlanManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        public abstract void onComplete(StructurePlan plan);

    }
