/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.model.entity.structure;

import com.google.common.base.Preconditions;
import com.settlercraft.model.entity.WorldLocation;
import com.settlercraft.model.entity.structure.Structure;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;

/**
 *
 * @author Chingo
 */
@Entity
public class StructureChest extends StructureEntity implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * Default JPA Constructor
     */
    protected StructureChest() {
    }

    /**
     * Constructor
     *
     * @param chestLocation The location of the chest
     * @param structure The structure this chest belongs to
     */
    public StructureChest(Location chestLocation, Structure structure) {
        super(new WorldLocation(chestLocation), structure);
        Preconditions.checkArgument(chestLocation.getBlock().getType() == Material.CHEST);
    }

    public Long getId() {
        return id;
    }

    public Chest getChest() {
        return (Chest) getLocation().getBlock().getState();
    }

    public Location getLocation() {
        return new Location(Bukkit.getWorld(wlocation.getWorld()), wlocation.getX(), wlocation.getY(), wlocation.getZ());
    }

    @Override
    public String toString() {
        return id + " : " + structure.getPlan() + ": TYPE=CHEST";
    }

}
