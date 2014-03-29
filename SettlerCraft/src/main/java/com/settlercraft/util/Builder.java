/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.util;

import com.google.common.base.Preconditions;
import com.settlercraft.model.structure.Structure;
import com.settlercraft.model.structure.StructurePlan;
import static com.settlercraft.util.LocationUtil.DIRECTION;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 *
 * @author Chingo
 */
public class Builder {

    private Builder() {
    }

    /**
     * Places an unfinished building at target location. The orientation of the building will be
     * determined by the yaw of the player
     *
     * @param player The player who places the building
     * @param target The target location
     * @param plan The structure plan of the structure
     */
    public static void placeStructure(Player player, Location target, StructurePlan plan) {
        Preconditions.checkArgument(player.isOnline());
        placeStructure(player, target, plan, LocationUtil.getDirection(player.getLocation().getYaw()));
    }

    /**
     * Places an unfinished building at target location. The orientation must be given
     *
     * @param player
     * @param target
     * @param plan
     * @param direction
     */
    public static void placeStructure(Player player, Location target, StructurePlan plan, DIRECTION direction) {
        Structure structure = new Structure(player, target, direction, plan.getConfig().getName());
        structure.buildLayer(0, direction);
        structure.buildLayer(1, direction);
//        structure.buildLayer(2, direction);
//        structure.buildLayer(3, direction);
//        structure.buildLayer(4, direction);
    }



//    public static void instantBuildStructure(Location playerLocation, Location target, SchematicObject schematic) {
//        DIRECTION direction = LocationUtil.getDirection(playerLocation.getYaw());
//        int[] mods = LocationgetModifiers(direction);
//        int xMod = mods[0];
//        int zMod = mods[1];
//        Iterator<BlockData> it = schematic.getBlocksSorted().iterator();
//        if (direction == DIRECTION.NORTH || direction == DIRECTION.SOUTH) {
//            for (int y = 0; y < schematic.height; y++) {
//                for (int z = schematic.length; z > 0; z--) {
//                    for (int x = 0; x < schematic.width; x++) {
//                        Block b = target.clone().add(x * xMod, y, z * zMod).getBlock();
//                        BlockData d = it.next();
//                        b.setType(d.getMaterial());
//                        b.setData(d.getData());
//                    }
//                }
//            }
//        } else {
//            for (int y = 0; y < schematic.height; y++) {
//                for (int z = schematic.length; z > 0; z--) {
//                    for (int x = 0; x < schematic.width; x++) {
//                        target.clone().add(z * zMod, y, x * xMod).getBlock().setType(it.next().getMaterial());
//                    }
//                }
//            }
//        }
//    }


    
 
    
    


}
