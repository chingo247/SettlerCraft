/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercrafttownapi.entities.container;

import java.io.Serializable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 *
 * @author Chingo
 */
@Entity
public class DepositChest implements Serializable {
    
    @Id
    @GeneratedValue
    private Long id;
    
    @Embedded
    private Chest chest;
    
    private String depositProfile;
    

    public DepositChest(Chest chest, String depositProfile) {
        Block b = chest.getLocation().getWorld().getBlockAt(chest.getLocation().getX(), chest.getLocation().getY(),chest.getLocation().getZ());
        if(b == null || b.getType() != Material.CHEST) {
            throw new AssertionError("Block should be of type chest");
        }
        this.chest = chest;
        this.depositProfile = depositProfile;
    }

    public Chest getChest() {
        return chest;
    }

    public String getDepositProfile() {
        return depositProfile;
    }

    public Long getId() {
        return id;
    }

    
}
