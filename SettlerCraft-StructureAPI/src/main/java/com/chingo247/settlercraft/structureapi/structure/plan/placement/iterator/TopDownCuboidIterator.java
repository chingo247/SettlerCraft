/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.structureapi.structure.plan.placement.iterator;

import com.chingo247.settlercraft.structureapi.structure.plan.placement.traversal.TopDownCuboidTraversal;
import com.sk89q.worldedit.Vector;
import java.util.Iterator;

/**
 *
 * @author Chingo
 */
public class TopDownCuboidIterator implements AreaIterator {
    
    private int cubeX;
    private int cubeY;
    private int cubeZ;

    public TopDownCuboidIterator() {
        this.cubeX = 16;
        this.cubeY = 16;
        this.cubeZ = 16;
    }
    
    public TopDownCuboidIterator(int cubeX, int cubeY, int cubeZ) {
        this.cubeX = cubeX;
        this.cubeY = cubeY;
        this.cubeZ = cubeZ;
    }
    

    @Override
    public Iterator<Vector> iterate(Vector size) {
        int cubx = Math.min(size.getBlockX(), cubeX);
        int cuby = Math.min(size.getBlockX(), cubeY);
        int cubz = Math.min(size.getBlockX(), cubeZ);
        return new TopDownCuboidTraversal(size, cubx, cuby, cubz);
    }
    
}
