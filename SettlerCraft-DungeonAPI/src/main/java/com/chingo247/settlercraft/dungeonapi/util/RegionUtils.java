/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.dungeonapi.util;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.regions.Region;
import java.util.Iterator;

/**
 *
 * @author Chingo
 */
public class RegionUtils {
    
    public static boolean intersects(Region regionA, Region regionB) {
        for(Iterator<BlockVector> it = regionA.iterator(); it.hasNext();) {
            BlockVector v = it.next();
            if(regionB.contains(v)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean covers(Region region, Region regionToCheck) {
        for(Iterator<BlockVector> it = regionToCheck.iterator(); it.hasNext();) {
            BlockVector v = it.next();
            if(!region.contains(v)) {
                return false;
            }
        }
        return true;
    }
    
}
