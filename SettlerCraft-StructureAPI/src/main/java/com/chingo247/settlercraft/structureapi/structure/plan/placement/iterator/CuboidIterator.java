/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.structureapi.structure.plan.placement.iterator;

import com.chingo247.settlercraft.structureapi.structure.plan.placement.traversal.CuboidTraversal;
import com.sk89q.worldedit.Vector;
import java.util.Iterator;

/**
 *
 * @author Chingo
 */
public class CuboidIterator implements AreaIterator {
    
    private int cubeX;
    private int cubeY;
    private int cubeZ;

    public CuboidIterator(int cubeX, int cubeY, int cubeZ) {
        this.cubeX = cubeX;
        this.cubeY = cubeY;
        this.cubeZ = cubeZ;
    }

    @Override
    public Iterator<Vector> iterate(Vector size) {
        int cubx = Math.min(size.getBlockX(), cubeX);
        int cuby = Math.min(size.getBlockY(), cubeY);
        int cubz = Math.min(size.getBlockZ(), cubeZ);
        return new CuboidTraversal(size, cubx, cuby, cubz);
    }
    
}
