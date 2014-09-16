/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.module.structureapi.plan.concurrent;

import com.sc.module.structureapi.plan.Schematic;
import com.sk89q.worldedit.data.DataException;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Chingo
 * @deprecated 
 */
public abstract class SchematicTask implements Runnable {
    
    private final File schematicFile;
    
    public SchematicTask(File schematic) {
        this.schematicFile = schematic;
    }

    @Override
    public void run() {
        try {
            Schematic schematic = Schematic.load(schematicFile);
            onComplete(schematic);
        } catch (IOException | DataException ex) {
            Logger.getLogger(SchematicTask.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public abstract void onComplete(Schematic schematic);
    
}
