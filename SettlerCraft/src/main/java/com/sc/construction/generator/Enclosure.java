/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.construction.generator;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import org.bukkit.scheduler.BukkitTask;

/**
 *
 * @author Chingo
 */
public class Enclosure extends CuboidClipboard {
    public static final int START_HEIGHT = 1; // 1 block above ground level
    private final int height;
    private final int material;
    private final CuboidClipboard enclosure; // The enclosure
    private BukkitTask task;

    public Enclosure(CuboidClipboard structure, int height, int material) {
        super(new BlockVector(structure.getWidth(), START_HEIGHT + height, structure.getLength()));
        Preconditions.checkArgument(height > 0);
        this.height = height;
        this.material = material;
        this.enclosure = new CuboidClipboard(
                new BlockVector(structure.getWidth(), START_HEIGHT + height, structure.getLength()));

        for (int z = 0; z < enclosure.getLength(); z+= (enclosure.getLength() - 1)) {
            for (int x = 0; x < enclosure.getWidth(); x++) {
                for (int y = START_HEIGHT; y < getHeight(); y++) {
                    Vector v = new BlockVector(x, y, z);
                    enclosure.setBlock(v, new BaseBlock(material));
                }
            }
        }
        
        for(int z = 1; z < enclosure.getLength() - 1; z++) {
            for(int x = 0; x < enclosure.getWidth(); x += (enclosure.getWidth()-1)) {
               for (int y = START_HEIGHT; y < getHeight(); y++) {
                    Vector v = new BlockVector(x, y, z);
                    enclosure.setBlock(v, new BaseBlock(material));
                } 
            }
        }
    }

    public CuboidClipboard getClipboard() {
        return enclosure;
    }

    public int getEnclosureHeight() {
        return height;
    }

    public int getMaterial() {
        return material;
    }
    
   
    
    
    
    
    
    
    
}
