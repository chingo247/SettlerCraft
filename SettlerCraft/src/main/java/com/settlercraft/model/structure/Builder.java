/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.settlercraft.model.structure;

import com.settlercraft.util.schematic.model.BlockData;
import com.settlercraft.util.schematic.model.SchematicObject;
import java.util.Iterator;
import java.util.Set;
import org.bukkit.Location;

/**
 *
 * @author Chingo
 */
public class Builder {
    
    public static void build(Location location, Structure structure) {
        SchematicObject sObj = structure.getSchematic();
        
        
        
    }
    
    private void BuildLayer(Location location, SchematicObject obj, int layer) {
        
        Iterator<BlockData> data = obj.getBlocksLayered().get(layer).iterator();
        
        int start = location.getBlockX();
        
        for(int x = start; x < start + obj.getWidth(); x++) {
            
        }
    }
    
}
