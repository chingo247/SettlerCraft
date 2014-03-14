/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.grid;

import org.bukkit.Location;
import org.bukkit.block.Block;

/**
 * A BuildGrid uses chunk locations to determine the grid
 * @author Chingo
 */
public class BuildGrid {

    public static GridLot getGridLot(Location loc) {
        GridLot[] gridLots = getGridLotsOfChunk(loc);
        for(GridLot lot : gridLots) {
            if(lot.onLot(loc)) return lot;
        }
        throw new AssertionError("unreachable");
    }

    public static GridLot[] getGridLotsOfChunk(Location loc) {
        GridLot[] gridLots = new GridLot[16];

        Block block = loc.getChunk().getBlock(0, 0, 0);
        int startX = block.getX();
        int startZ = block.getZ();

        int count = 0;
        for (int x = startX; x < startX + 16; x += 4) {
            for (int z = startZ; z < startZ + 16; z += 4) {
                gridLots[count] = new GridLot(x, z);
                count++;
            }
        }

        return gridLots;
    }
    
}
