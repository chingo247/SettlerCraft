/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.util;

import com.settlercraft.util.schematic.model.BlockData;
import com.settlercraft.util.schematic.model.SchematicObject;
import com.settlercraft.util.schematic.util.SchematicUtil;
import java.io.File;
import java.util.Iterator;
import org.bukkit.Location;
import org.bukkit.Material;

/**
 *
 * @author Chingo
 */
public class Foundations {

  private Foundations() {
  }

  public static void createDefaultFoundation(Location playerLocation, Location target, SchematicObject schematic, Material material) {
    Location direction = playerLocation.clone().subtract(target);

    System.out.println(direction);

    int xMod; // = (direction.getX() > 0) ? -1 : 1;
    int zMod; //= (direction.getZ() > 0) ? -1 : 1;
    System.out.println(LocationUtil.getDirection(playerLocation.getYaw()).name());
    switch (LocationUtil.getDirection(playerLocation.getYaw())) {
      case NORTH:
        zMod = 1;
        xMod = -1;
        break;
      case EAST:
        zMod = 1;
        xMod = 1;
        break;
      case SOUTH:
        zMod = -1;
        xMod = 1;
        break;
      case WEST:
        zMod = -1;
        xMod = -1;
        break;
      default:
        throw new AssertionError("Unreachable");
    }

    for (int z = 0; z < schematic.length; z++) {
      for (int x = 0; x < schematic.width; x++) {
        Location loc = target.clone().add(-x * xMod, 0, -z * zMod);
//                System.out.println("X: " + loc.getBlockX() + " Y:"+ loc.getBlockY() +  " Z:" + loc.getBlockZ());
        loc.getBlock().setType(material);
      }
    }

    //VISUALIZE BUILDING TEST 
    Iterator<BlockData> it = schematic.getBlocksSorted().iterator();
    for (int y = 0; y < schematic.height; y++) {
      for (int z = 0; z < schematic.length; z++) {
        for (int x = 0; x < schematic.width; x++) {
          target.clone().add(x*xMod, y, z*zMod).getBlock().setType(it.next().getMaterial());
        }
      }
    }
  }

}
