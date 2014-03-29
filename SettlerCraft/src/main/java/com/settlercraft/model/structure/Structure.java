/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.model.structure;

import com.avaje.ebean.validation.NotEmpty;
import com.avaje.ebean.validation.NotNull;
import com.google.common.base.Preconditions;
import com.settlercraft.main.StructurePlanRegister;
import com.settlercraft.util.LocationUtil;
import com.settlercraft.util.LocationUtil.DIRECTION;
import static com.settlercraft.util.LocationUtil.DIRECTION;
import com.settlercraft.util.schematic.model.BlockData;
import com.settlercraft.util.schematic.model.SchematicObject;
import java.io.Serializable;
import java.util.Iterator;
import java.util.TreeSet;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 *
 * @author Chingo
 */
@Entity
@Table(name = "sc_structure")
public class Structure implements Serializable {

    @Id
    @GeneratedValue
    private long id;
    @NotNull
    private String owner;
    @NotNull
    private Location location;

    @Embedded
    @NotNull
    private StructureChest structureChest;

    @Embedded
    @NotNull
    private StructureSign structureSign;

    @NotNull
    @NotEmpty
    private String plan;

    protected Structure() {
    }

    
    
    public Structure(Player owner, Location target, DIRECTION direction, String plan) {
        Preconditions.checkNotNull(StructurePlanRegister.getPlan(plan));
        this.owner = owner.getName();
        this.location = target;
        this.plan = plan;
        
        
        //TODO Check if structure has own foundation schematic, otherwise structure should use the default foundation
        createDefaultFoundation(target, direction, StructurePlanRegister.getPlan(plan).getSchematic());
        
        Location chestLocation = placeProgressEntity(target, direction, 1, Material.CHEST); 
        Location signLocation = placeProgressEntity(target, direction, 2, Material.SIGN_POST);
        structureChest = new StructureChest(chestLocation);
        structureSign = new StructureSign(signLocation);
    }


    private void createDefaultFoundation(Location target, DIRECTION direction, SchematicObject schematic) {
        int[] mods = LocationUtil.getModifiers(direction);
        int xMod = mods[0];
        int zMod = mods[1];
        if (direction == LocationUtil.DIRECTION.NORTH || direction == LocationUtil.DIRECTION.SOUTH) {
            for (int z = schematic.length - 1; z >= 0; z--) {
                for (int x = 0; x < schematic.width; x++) {
                    target.clone().add(x * xMod, 0, z * zMod).getBlock().setType(Material.COBBLESTONE);
                }
            }
        } else {
            for (int z = schematic.length - 1; z >= 0; z--) {
                for (int x = 0; x < schematic.width; x++) {
                    target.clone().add(z * zMod, 0, x * xMod).getBlock().setType(Material.COBBLESTONE);
                }
            }
        }
    }
    
    public void buildLayer(int layer, DIRECTION direction) {
        StructurePlan sp = StructurePlanRegister.getPlan(plan);
        Iterator<BlockData> it = sp.getSchematic().getBlocksFromLayer(layer).iterator();
//        System.out.println(sp.getSchematic().getBlocksSorted().size());
//        System.out.println(sp.getSchematic().getBlocksFromLayer(layer).size());
        SchematicObject schematic = sp.getSchematic();
        
        int[] mods = LocationUtil.getModifiers(direction);
        int xMod = mods[0];
        int zMod = mods[1];
        
        if (direction == LocationUtil.DIRECTION.NORTH || direction == LocationUtil.DIRECTION.SOUTH) {
            for (int z = schematic.length - 1; z >= 0; z--) {
                for (int x = 0; x < schematic.width; x++) {
                    location.clone().add(x * xMod, layer, z * zMod).getBlock().setType(it.next().getMaterial());
                }
            }
        } else {
            for (int z = schematic.length - 1; z >= 0; z--) {
                for (int x = 0; x < schematic.width; x++) {
                    location.clone().add(z * zMod, layer, x * xMod).getBlock().setType(it.next().getMaterial());
                }
            }
        }
    }

    
    
    
    private Location placeProgressEntity(Location target, LocationUtil.DIRECTION direction, int yOffset, Material m) {
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
