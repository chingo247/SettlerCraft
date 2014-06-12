/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.structure.sync;

import com.sc.StructureBlock;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import java.util.PriorityQueue;

/**
 *
 * @author Chingo
 */
public class StructureBlockQueue extends PriorityQueue<StructureBlock> {

    public StructureBlockQueue(CuboidClipboard whole) {
        for(int y = 0; y < whole.getHeight(); y++) {
            for(int x = 0; x < whole.getWidth(); x++) {
                for(int z = 0; z < whole.getLength(); z++) {
                    Vector v = new BlockVector(x, y, z);
                    BaseBlock b = whole.getBlock(v);
                    if(b != null) {
                        add(new StructureBlock(v, whole.getBlock(v)));
                    }
                }
            }
        }
    }
    
    
    
}
