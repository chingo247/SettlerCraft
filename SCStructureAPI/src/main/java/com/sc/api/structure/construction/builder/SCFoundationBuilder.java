/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.api.structure.construction.builder;

import com.sc.api.structure.construction.strategies.FoundationStrategy;
import com.sc.api.structure.model.structure.Structure;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Location;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.regions.CuboidRegion;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Material;

/**
 *
 * @author Chingo
 */
public class SCFoundationBuilder {

    /**
     * A foundation will be created beneath the structure. A foundation is
     * doesnt have any functionality its just there to give the player some
     * feedback. And also clears the construction site from any blocks
     *
     * @param player The player
     * @param structure The structure
     * @param strategy The strategy
     * @param material The material to be used
     */
    static void placeFoundation(EditSession session, Structure structure, FoundationStrategy strategy, Material material, boolean autoflush) {
        switch (strategy) {
            case DEFAULT:
                placeDefault(session, structure, material, autoflush);
                break;
            case FANCY:
                placeFancyFoundation(structure, material);
                break;
            default: throw new AssertionError("No action known for: " + strategy);
        }
    }
    
    static void placeDefault(EditSession session, Structure structure, Material material, boolean autoflush) {
        Location pos1 = structure.getDimension().getStart();
        Location pos2 = new Location(pos1.getWorld(), new BlockVector(structure.getDimension().getEndX(), 1, structure.getDimension().getEndZ()));
        
        try {
            session.makeCuboidFaces(new CuboidRegion(pos1.getWorld(), pos1.getPosition(), pos2.getPosition()), new BaseBlock(material.getId()));
            if(autoflush) {
                session.flushQueue();
            }
        }
        catch (MaxChangedBlocksException ex) {
            Logger.getLogger(SCFoundationBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    

//    static CuboidClipboard generateDefault(Structure structure, Material material) {
//        CuboidClipboard cl = structure.getPlan().getSchematic();
//        int width = cl.getWidth();
//        int length = cl.getLength();
//        
//        CuboidClipboard foundation = new CuboidClipboard(new BlockVector(width, 1, length));
//        for(int x = 0; x < width; x++) {
//            for(int z = 0; z < length; z++) {
//                foundation.setBlock(new BlockVector(x, 0, z), new BaseBlock(material.getId()));
//            }
//        }
//        return foundation;
//    }

    static CuboidClipboard placeFancyFoundation(Structure structure, Material material) {
        throw new UnsupportedOperationException("This feature is not supported yet");
    }

}
