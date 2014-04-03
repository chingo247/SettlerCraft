/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.model.entity.structure;

import com.google.common.base.Preconditions;
import com.settlercraft.model.entity.WorldLocation;
import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

/**
 *
 * @author Chingo
 */
@Entity
public class StructureSign implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "structure")
    private Structure mainStructure;

    @Embedded
    private WorldLocation wlocation;

    /**
     * Default JPA Constructor
     */
    protected StructureSign() {
    }

    public StructureSign(Location signLocation, Structure structure) {
        Preconditions.checkArgument(signLocation.getBlock().getType() == Material.SIGN_POST);
        this.mainStructure = structure;
        this.wlocation = new WorldLocation(signLocation);
    }

    public Long getId() {
        return id;
    }

    public Structure getStructure() {
        return mainStructure;
    }

    public void setStructure(Structure structure) {
        this.mainStructure = structure;
    }

        public Location getLocation() {
        return new Location(Bukkit.getWorld(wlocation.getWorld()), wlocation.getX(), wlocation.getY(), wlocation.getZ());
    }

    @Override
    public String toString() {
        return id + " : " + mainStructure.getPlan() + ": TYPE=SIGN";
    }
    
        
}
