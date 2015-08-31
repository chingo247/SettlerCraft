/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.dungeonapi.util;

import com.chingo247.settlercraft.dungeonapi.IDungeonFloor;
import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldedit.Vector2D;
import java.util.Random;

/**
 *
 * @author Chingo
 */
public class RandomUtil {
    
    public static Vector2D pickPosition(Random random, Vector2D min, Vector2D max) {
        Vector2D rMin = new BlockVector2D(Math.min(min.getBlockX(), max.getBlockX()), Math.min(min.getBlockZ(), max.getBlockZ()));
        Vector2D rMax = new BlockVector2D(Math.max(min.getBlockX(), max.getBlockX()), Math.max(min.getBlockZ(), max.getBlockZ()));
        return new Vector2D(pickInRange(random, rMin.getBlockX(), rMax.getBlockX()), pickInRange(random, rMin.getBlockZ(), rMax.getBlockZ()));
    }
    
    public static Vector2D pickPosition(Random random, IDungeonFloor floor) {
        return pickPosition(random, Vector2D.ONE, new Vector2D(floor.getWidth()-1, floor.getLength()-1));
    }
    
    public static int pickInRange(Random random, int min, int max) {
        return random.nextInt((max - min) + 1) + min;
    }
    
    public static int pickInRange(Random random, int max) {
        return random.nextInt(max) ;
    }
    
    
    
}
