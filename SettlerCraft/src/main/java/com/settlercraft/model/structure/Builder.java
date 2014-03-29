/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.model.structure;

import com.avaje.ebean.validation.NotEmpty;
import com.avaje.ebean.validation.NotNull;
import com.settlercraft.main.StructurePlanRegister;
import com.settlercraft.util.LocationUtil;
import com.settlercraft.util.LocationUtil.DIRECTION;
import com.settlercraft.util.schematic.model.BlockData;
import com.settlercraft.util.schematic.model.SchematicObject;
import java.io.Serializable;
import java.util.Iterator;
import javax.persistence.Embeddable;
import javax.persistence.OneToOne;
import org.bukkit.Location;
import org.bukkit.Material;

/**
 *
 * @author Chingo
 */
@Embeddable
public class Builder implements Serializable {

    
    @NotNull
    @NotEmpty
    private String plan;
    
    @NotNull
    @OneToOne
    private final Structure mainStructure;
    
    private int currentLayer;

    Builder(String plan, Structure structure) {
        this.mainStructure = structure;
        this.currentLayer = 0;
        //TODO Check if structure has own foundation schematic, otherwise structure should use the default foundation
        createDefaultFoundation();
    }
    
    
    
    private void createDefaultFoundation() {
        DIRECTION direction = mainStructure.getDirection();
        SchematicObject schematic = mainStructure.getPlan().getSchematic();
        int[] mods = LocationUtil.getModifiers(direction);
        int xMod = mods[0];
        int zMod = mods[1];
        if (direction == LocationUtil.DIRECTION.NORTH || direction == LocationUtil.DIRECTION.SOUTH) {
            for (int z = schematic.length - 1; z >= 0; z--) {
                for (int x = 0; x < schematic.width; x++) {
                    mainStructure.getLocation().clone().add(x * xMod, 0, z * zMod).getBlock().setType(Material.COBBLESTONE);
                }
            }
        } else {
            for (int z = schematic.length - 1; z >= 0; z--) {
                for (int x = 0; x < schematic.width; x++) {
                    mainStructure.getLocation().clone().add(z * zMod, 0, x * xMod).getBlock().setType(Material.COBBLESTONE);
                }
            }
        }
    }
    
    /**
     * Builds the corresponding layer of this building, whether the precoditions are met or not
     * @param layer The layer to build
     */
    public void buildLayer(int layer) {
        StructurePlan sp = StructurePlanRegister.getPlan(plan);
        Iterator<BlockData> it = sp.getSchematic().getBlocksFromLayer(layer).iterator();
        SchematicObject schematic = sp.getSchematic();
        DIRECTION direction = mainStructure.getDirection();
        
        int[] mods = LocationUtil.getModifiers(direction);
        int xMod = mods[0];
        int zMod = mods[1];
        
        if (direction == LocationUtil.DIRECTION.NORTH || direction == LocationUtil.DIRECTION.SOUTH) {
            for (int z = schematic.length - 1; z >= 0; z--) {
                for (int x = 0; x < schematic.width; x++) {
                    mainStructure.getLocation().clone().add(x * xMod, 0, z * zMod).getBlock().setType(it.next().getMaterial());
                }
            }
        } else { // SWAP X AND Z
            for (int z = schematic.length - 1; z >= 0; z--) {
                for (int x = 0; x < schematic.width; x++) {
                    mainStructure.getLocation().clone().add(z * zMod, 0, x * xMod).getBlock().setType(it.next().getMaterial());
                }
            }
        }
    }

    Location placeProgressEntity(Location target, LocationUtil.DIRECTION direction, int yOffset, Material m) {
        int[] mods = LocationUtil.getModifiers(direction);
        int xMod = mods[0];
        int zMod = mods[1];
        Location loc;
        if (direction == LocationUtil.DIRECTION.NORTH || direction == LocationUtil.DIRECTION.SOUTH) {
            loc = target.clone().add(0 * xMod, yOffset, -1 * zMod);
        } else {
            loc = target.clone().add(-1 * zMod, yOffset, 0 * xMod);
        }
        loc.getBlock().setType(m);
        return loc;
    }

}
