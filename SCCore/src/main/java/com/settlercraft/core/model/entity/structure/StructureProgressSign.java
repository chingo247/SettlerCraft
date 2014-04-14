/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.core.model.entity.structure;

import com.google.common.base.Preconditions;
import com.settlercraft.core.model.entity.WorldLocation;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

/**
 * Displays info about the structure it's construction progress
 * @author Chingo
 */
@Entity
public class StructureProgressSign extends StructureEntity implements Serializable {

    @Id
    @GeneratedValue
    private Long id;
    /**
     * Default JPA Constructor
     */
    protected StructureProgressSign() {
    }

    public StructureProgressSign(Location signLocation, Structure structure) {
        super(new WorldLocation(signLocation), structure);
        Preconditions.checkArgument(signLocation.getBlock().getType() == Material.SIGN_POST);
        this.wlocation = new WorldLocation(signLocation);
    }

    public Long getId() {
        return id;
    }

    public Location getLocation() {
        return new Location(Bukkit.getWorld(wlocation.getWorld()), wlocation.getX(), wlocation.getY(), wlocation.getZ());
    }


}
