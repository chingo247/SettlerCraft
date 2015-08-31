/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.dungeonapi;

import com.chingo247.settlercraft.dungeonapi.util.Sizes;
import com.google.common.base.Preconditions;

/**
 *
 * @author Chingo
 */
public class DungeonOptions {
    
    public static final int MINIMUM_SIZE = 20;
    public static final int MAXIMUM_SIZE = 20;
    
    private int minRoomSize;
    private int maxRoomSize;
    private final long seed;
    private int density = 10;
    
    public DungeonOptions(long seed) {
        this.seed = seed;
        this.minRoomSize = Sizes.SMALL;
        this.maxRoomSize = Sizes.LARGE;
    }

   
    
    public void setDensity(int density) {
        Preconditions.checkArgument(density > 0, "Density must be > 0");
        this.density = density;
    }
    
    public int getDensity() {
        return density;
    }
    
    

    public long getSeed() {
        return seed;
    }

    public DungeonOptions setMaxRoomSize(int maxRoomSize) {
        if(this.maxRoomSize < 4) {
            throw new IllegalArgumentException("Min roomsize is 4");
        }
        this.maxRoomSize = maxRoomSize;
        return this;
    }

    public DungeonOptions setMinRoomSize(int minRoomSize) {
        this.minRoomSize = minRoomSize;
        return this;
    }

    public int getMaxRoomSize() {
        return maxRoomSize;
    }

    public int getMinRoomSize() {
        return minRoomSize;
    }
    
    
}
