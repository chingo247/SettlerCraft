/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.util.grid;

import org.bukkit.Location;

/**
 * A gridlot is one square of 4x4, each gridlot knows the location of it's neighbour
 * this way building lots won't depends on Chunks but on the grid
 * @author Chingo
 */
public class GridLot {

    public static final int DIMENSION = 4;
    public final int startX;
    public final int startZ;

    public enum NEIGHBOUR {

        NORTH,
        SOUTH,
        EAST,
        WEST
    }

    public GridLot(int startX, int startZ) {
        this.startX = startX;
        this.startZ = startZ;
    }

    public GridLot getNeighbour(NEIGHBOUR direction) {
        switch (direction) {
            case EAST:
                return new GridLot(startX + DIMENSION, startZ);
            case WEST:
                return new GridLot(startX - DIMENSION, startZ);
            case SOUTH:
                return new GridLot(startX, startZ - DIMENSION);
            case NORTH:
                return new GridLot(startX, startZ + DIMENSION);
            default: throw new AssertionError(direction + " : unrecognized" );
        }
    }
    
    boolean onLot(Location loc) {
        int x = loc.getBlockX();
        int z = loc.getBlockZ();
        
        return (x >= startX && x <= startX + DIMENSION) && (z >= startZ && z <= startZ + DIMENSION);
                
    }

    @Override
    public String toString() {
        return " {start [" + startX + "," + startZ + "]" + "end [" + (startX + DIMENSION) + "," + (startZ + DIMENSION) + "]}";
    }

}
