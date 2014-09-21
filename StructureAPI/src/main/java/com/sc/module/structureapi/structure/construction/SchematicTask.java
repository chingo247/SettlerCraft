/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.module.structureapi.structure.construction;

import com.sc.module.structureapi.structure.schematic.SchematicManager;
import com.sk89q.worldedit.data.DataException;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Chingo
 */
public class SchematicTask implements Runnable {
    
    private final File schematic;

    public SchematicTask(File schematic) {
        this.schematic = schematic;
    }

    @Override
    public void run() {
        try {
            SchematicManager.getInstance().load(schematic);
        } catch (IOException | DataException ex) {
            Logger.getLogger(SchematicTask.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    
}
